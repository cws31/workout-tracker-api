package com.workouttrackerapi.workout.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import com.workouttrackerapi.auth.model.Users;
import com.workouttrackerapi.common.exceptions.ExerciseNotFoundException;
import com.workouttrackerapi.common.exceptions.UnauthorizedException;
import com.workouttrackerapi.common.exceptions.WorkoutAlreadyCompletedException;
import com.workouttrackerapi.common.exceptions.WorkoutNotFoundException;
import com.workouttrackerapi.common.exceptions.WorkoutSlotBookedException;
import com.workouttrackerapi.exercise.model.Exercises;
import com.workouttrackerapi.exercise.repository.ExerciseRepository;
import com.workouttrackerapi.workout.dto.WorkoutCompletionRequest;
import com.workouttrackerapi.workout.dto.WorkoutCompletionResponse;
import com.workouttrackerapi.workout.dto.WorkoutDetaiilsResponse;
import com.workouttrackerapi.workout.dto.WorkoutRequest;
import com.workouttrackerapi.workout.dto.WorkoutResponse;
import com.workouttrackerapi.workout.dto.WorkoutSetRequest;
import com.workouttrackerapi.workout.dto.Workout_Exercises_Request;
import com.workouttrackerapi.workout.dto.reports.MonthlySummaryResponse;
import com.workouttrackerapi.workout.dto.reports.ProgressResponse;
import com.workouttrackerapi.workout.dto.reports.UpdateRequest;
import com.workouttrackerapi.workout.dto.reports.WorkoutHistoryResponse;

import com.workouttrackerapi.workout.enums.STATUS;
import com.workouttrackerapi.workout.model.WorkoutHistory;
import com.workouttrackerapi.workout.model.WorkoutSets;
import com.workouttrackerapi.workout.model.WorkoutExercises;
import com.workouttrackerapi.workout.model.Workouts;
import com.workouttrackerapi.workout.repository.*;

import jakarta.transaction.Transactional;

@Service
public class WorkoutService {

    private final WorkOutRepository workOutRepository;
    private final ExerciseRepository eRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public WorkoutService(WorkOutRepository workOutRepository, ExerciseRepository eRepository,
            WorkoutHistoryRepository workoutHistoryRepository, WorkoutExerciseRepository workoutExerciseRepository) {
        this.workOutRepository = workOutRepository;
        this.eRepository = eRepository;
        this.workoutHistoryRepository = workoutHistoryRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    public WorkoutResponse createWorkOut(WorkoutRequest wpRequest, Users user) {
        Workouts existed_workout = workOutRepository.findByUsersIdAndScheduledDateAndScheduledTime(user.getId(),
                wpRequest.getScheduled_date(), wpRequest.getScheduled_time());
        if (existed_workout != null) {
            throw new WorkoutSlotBookedException(
                    "You already have a workout scheduled at this time: " + existed_workout.getTitle()
                            + " " + existed_workout.getScheduledDate()
                            + " " + existed_workout.getScheduledTime());
        }

        Workouts workouts = new Workouts();
        workouts.setTitle(wpRequest.getTitle());
        workouts.setDescription(wpRequest.getDescription());
        workouts.setScheduledDate(wpRequest.getScheduled_date());
        workouts.setScheduledTime(wpRequest.getScheduled_time());
        workouts.setUsers(user);
        workouts.setStaus(STATUS.PLANNED);

        List<WorkoutExercises> workoutExercisesList = new ArrayList<>();

        for (Workout_Exercises_Request req : wpRequest.getWorkout_Exercises_Request()) {
            Exercises exe = eRepository.findById(req.getId())
                    .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found for id " + req.getId()));

            WorkoutExercises workoutExercise = new WorkoutExercises();
            workoutExercise.setExercises(exe);
            workoutExercise.setWorkouts(workouts);

            // Map individual sets
            List<WorkoutSets> workoutSetsList = new ArrayList<>();
            if (req.getSets() != null) {
                for (WorkoutSetRequest setReq : req.getSets()) {
                    WorkoutSets set = new WorkoutSets();
                    set.setSetNumber(setReq.getSetNumber());
                    set.setSetType(setReq.getSetType() != null ? setReq.getSetType() : "WORKING");
                    set.setReps(setReq.getReps());
                    set.setWeight(setReq.getWeight());
                    set.setWorkoutExercises(workoutExercise);
                    workoutSetsList.add(set);
                }
            }
            workoutExercise.setWorkoutSets(workoutSetsList);
            workoutExercisesList.add(workoutExercise);
        }

        workouts.setWorkout_Exercises(workoutExercisesList);
        Workouts savedWorkouts = workOutRepository.save(workouts);

        return mapToResponse(savedWorkouts);
    }

    public WorkoutResponse updateWorkout(Long id, UpdateRequest updateRequest,
            Users user) {

        Workouts workouts = workOutRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Workout not found"));

        if (updateRequest.getChangeScheduedDate() != null) {
            workouts.setScheduledDate(updateRequest.getChangeScheduedDate());
        }

        if (updateRequest.getChangeScheduedTime() != null) {
            workouts.setScheduledTime(updateRequest.getChangeScheduedTime());
        }

        if (updateRequest.getAddExecise() != null) {

            for (Workout_Exercises_Request req : updateRequest.getAddExecise()) {

                Exercises exercise = eRepository.findById(req.getId())
                        .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));

                boolean exist = false;
                for (WorkoutExercises we : workouts.getWorkout_Exercises()) {
                    if (we.getExercises().getId().equals(req.getId())) {
                        if (req.getSets() != null) {
                            List<WorkoutSets> updatedSetsList = new ArrayList<>();
                            for (WorkoutSetRequest setReq : req.getSets()) {
                                WorkoutSets set = new WorkoutSets();
                                set.setSetNumber(setReq.getSetNumber());
                                set.setSetType(setReq.getSetType() != null ? setReq.getSetType() : "WORKING");
                                set.setReps(setReq.getReps());
                                set.setWeight(setReq.getWeight());
                                set.setWorkoutExercises(we);
                                updatedSetsList.add(set);
                            }
                            we.getWorkoutSets().clear();
                            we.getWorkoutSets().addAll(updatedSetsList);
                        }
                        exist = true;
                        break;
                    }
                }

                if (!exist) {
                    WorkoutExercises workoutExercise = new WorkoutExercises();
                    workoutExercise.setExercises(exercise);
                    workoutExercise.setWorkouts(workouts);

                    List<WorkoutSets> workoutSetsList = new ArrayList<>();
                    if (req.getSets() != null) {
                        for (WorkoutSetRequest setReq : req.getSets()) {
                            WorkoutSets set = new WorkoutSets();
                            set.setSetNumber(setReq.getSetNumber());
                            set.setSetType(setReq.getSetType() != null ? setReq.getSetType() : "WORKING");
                            set.setReps(setReq.getReps());
                            set.setWeight(setReq.getWeight());
                            set.setWorkoutExercises(workoutExercise);
                            workoutSetsList.add(set);
                        }
                    }
                    workoutExercise.setWorkoutSets(workoutSetsList);
                    workouts.getWorkout_Exercises().add(workoutExercise);
                }
            }
        }

        if (updateRequest.getComments() != null) {
            workouts.setComments(updateRequest.getComments());
        }

        workOutRepository.save(workouts);

        return mapToResponse(workouts);
    }

    private WorkoutResponse mapToResponse(Workouts workout) {
        List<Workout_Exercises_Request> exerciseDtos = new ArrayList<>();

        for (WorkoutExercises we : workout.getWorkout_Exercises()) {
            Workout_Exercises_Request dto = new Workout_Exercises_Request();
            dto.setId(we.getExercises().getId());
            dto.setName(we.getExercises().getName());

            List<WorkoutSetRequest> setDtos = new ArrayList<>();
            if (we.getWorkoutSets() != null) {
                for (WorkoutSets ws : we.getWorkoutSets()) {
                    WorkoutSetRequest setDto = new WorkoutSetRequest();
                    setDto.setSetNumber(ws.getSetNumber());
                    setDto.setSetType(ws.getSetType());
                    setDto.setReps(ws.getReps());
                    setDto.setWeight(ws.getWeight());
                    setDtos.add(setDto);
                }
            }
            dto.setSets(setDtos);
            exerciseDtos.add(dto);
        }

        WorkoutResponse response = new WorkoutResponse();
        response.setId(workout.getId());
        response.setTitle(workout.getTitle());
        response.setDescription(workout.getDescription());
        response.setScheduledDate(workout.getScheduledDate());
        response.setScheduledTime(workout.getScheduledTime());
        response.setUserId(workout.getUsers().getId());
        response.setWorkout_Exercises_Request(exerciseDtos);

        return response;
    }

    @Transactional
    public void deleteWorkout(Users user, Long workoutid) {
        Long userid = user.getId();
        Optional<Workouts> existedWorkout = workOutRepository.findByIdAndUsersId(workoutid, userid);
        if (existedWorkout.isEmpty()) {
            throw new WorkoutNotFoundException("workout not found");
        }
        workOutRepository.delete(existedWorkout.get());

    }

    public WorkoutDetaiilsResponse getWorkoutDetails(Users user, Long workoutid) {
        Long userid = user.getId();
        Optional<Workouts> existedworkout = workOutRepository.findByIdAndUsersId(workoutid, userid);

        if (existedworkout.isEmpty()) {
            throw new WorkoutNotFoundException("workouts not found");
        }
        Workouts wk = existedworkout.get();

        List<Workout_Exercises_Request> list = new ArrayList<>();
        for (WorkoutExercises we : wk.getWorkout_Exercises()) {
            List<WorkoutSetRequest> setRequests = new ArrayList<>();
            if (we.getWorkoutSets() != null) {
                for (WorkoutSets ws : we.getWorkoutSets()) {
                    setRequests.add(new WorkoutSetRequest(
                            ws.getSetNumber(),
                            ws.getSetType(),
                            ws.getReps(),
                            ws.getWeight()));
                }
            }
            list.add(new Workout_Exercises_Request(
                    we.getExercises().getId(),
                    we.getExercises().getName(),
                    setRequests));
        }
        return new WorkoutDetaiilsResponse(wk.getId(), wk.getTitle(), wk.getDescription(), wk.getScheduledDate(),
                wk.getScheduledTime(), list);
    }

    public List<WorkoutDetaiilsResponse> getScheduledWorkouts(Users users, STATUS status) {

        Sort sort = Sort.by("scheduledDate").ascending()
                .and(Sort.by("scheduledTime").ascending());

        List<Workouts> responses = workOutRepository.findByUsersIdAndStaus(users.getId(), status, sort);

        List<WorkoutDetaiilsResponse> responselist = new ArrayList<>();

        for (Workouts wk : responses) {

            WorkoutDetaiilsResponse wd = new WorkoutDetaiilsResponse();

            wd.setTitle(wk.getTitle());
            wd.setDescription(wk.getDescription());
            wd.setScheduledDate(wk.getScheduledDate());
            wd.setScheduledTime(wk.getScheduledTime());
            wd.setId(wk.getId());

            List<Workout_Exercises_Request> exerciseList = new ArrayList<>();

            for (WorkoutExercises we : wk.getWorkout_Exercises()) {

                List<WorkoutSetRequest> setRequests = new ArrayList<>();
                if (we.getWorkoutSets() != null) {
                    for (WorkoutSets ws : we.getWorkoutSets()) {
                        setRequests.add(new WorkoutSetRequest(
                                ws.getSetNumber(),
                                ws.getSetType(),
                                ws.getReps(),
                                ws.getWeight()));
                    }
                }

                Workout_Exercises_Request dto = new Workout_Exercises_Request();
                dto.setName(we.getExercises().getName());
                dto.setId(we.getExercises().getId());
                dto.setSets(setRequests);

                exerciseList.add(dto);
            }

            wd.setExercises(exerciseList);

            responselist.add(wd);
        }

        return responselist;
    }

    public Workouts completeWorkout(Long workoutId, Users user, WorkoutCompletionRequest request) {
        Workouts workout = workOutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout not found"));

        if (!workout.getUsers().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to access this workout");
        }

        if (workout.getStaus() == STATUS.COMPLETED) {
            throw new WorkoutAlreadyCompletedException("Workout already completed");
        }

        workout.setStaus(STATUS.COMPLETED);
        workOutRepository.save(workout);

        WorkoutHistory history = new WorkoutHistory();
        history.setWorkout(workout);
        history.setCompletionDate(LocalDate.now());
        history.setDuration(request.getDuration());
        history.setNotes(request.getNotes());

        workoutHistoryRepository.save(history);

        return workout;
    }

    public List<WorkoutHistoryResponse> getWorkoutHistory(Users user) {

        List<WorkoutHistory> historyList = workoutHistoryRepository.findByWorkout_Users_Id(user.getId());

        return historyList.stream()
                .map(h -> new WorkoutHistoryResponse(
                        h.getWorkout().getTitle(),
                        h.getCompletionDate(),
                        h.getDuration(),
                        h.getNotes()))
                .toList();
    }

    public ProgressResponse getProgress(Long exerciseId, Users user) {

        List<Object[]> results = workoutExerciseRepository.getExerciseProgress(
                exerciseId,
                user.getId(),
                STATUS.COMPLETED);

        if (results == null || results.isEmpty()) {
            return new ProgressResponse(0.0, 0.0, 0L);
        }

        Object[] result = results.get(0);

        Double maxWeight = result[0] != null ? ((Number) result[0]).doubleValue() : 0.0;
        Double avgReps = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
        Long totalSessions = result[2] != null ? ((Number) result[2]).longValue() : 0L;

        return new ProgressResponse(maxWeight, avgReps, totalSessions);
    }

    public MonthlySummaryResponse getMonthlySummary(int month, int year, Users user) {

        List<Object[]> results = workoutHistoryRepository
                .getMonthlySummary(user.getId(), month, year);

        if (results == null || results.isEmpty()) {
            return new MonthlySummaryResponse(0L, 0);
        }

        Object[] result = results.get(0);

        Long totalWorkouts = result[0] != null ? ((Number) result[0]).longValue() : 0L;
        Integer totalDuration = result[1] != null ? ((Number) result[1]).intValue() : 0;

        return new MonthlySummaryResponse(totalWorkouts, totalDuration);
    }

    public List<WorkoutSetRequest> getLastExercisePerformance(Long exerciseId, Users user) {

        List<WorkoutSets> lastSets = workoutExerciseRepository.findLastCompletedExerciseSets(
                exerciseId, user.getId(), STATUS.COMPLETED, PageRequest.of(0, 20));

        List<WorkoutSetRequest> response = new ArrayList<>();
        if (lastSets != null && !lastSets.isEmpty()) {

            for (WorkoutSets ws : lastSets) {
                response.add(new WorkoutSetRequest(
                        ws.getSetNumber(),
                        ws.getSetType(),
                        ws.getReps(),
                        ws.getWeight()));
            }
        }
        return response;
    }

    public List<String> checkAndCelebratePRs(Workouts workout, Users user) {
        List<String> newPrs = new ArrayList<>();

        for (WorkoutExercises we : workout.getWorkout_Exercises()) {
            Long exerciseId = we.getExercises().getId();
            String exerciseName = we.getExercises().getName();

            List<Object[]> progressData = workoutExerciseRepository.getExerciseProgress(exerciseId, user.getId(),
                    STATUS.COMPLETED);

            Double historicalMaxWeight = 0.0;
            if (progressData != null && !progressData.isEmpty() && progressData.get(0)[0] != null) {
                historicalMaxWeight = Double.valueOf(progressData.get(0)[0].toString());
            }

            double currentWorkoutMax = 0.0;
            if (we.getWorkoutSets() != null) {
                for (WorkoutSets set : we.getWorkoutSets()) {
                    if (set.getWeight() > currentWorkoutMax) {
                        currentWorkoutMax = set.getWeight();
                    }
                }
            }

            System.out.println("DEBUG: Exercise: " + exerciseName);
            System.out.println("DEBUG: Historical Max: " + historicalMaxWeight);
            System.out.println("DEBUG: Current Workout Max: " + currentWorkoutMax);

            if (currentWorkoutMax > historicalMaxWeight && currentWorkoutMax > 0) {
                newPrs.add("🎉 New PR on " + exerciseName + ": " + currentWorkoutMax + " kg!");
            }
        }
        return newPrs;
    }
}
