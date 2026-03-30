package com.workouttrackerapi.workout.service;

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
import com.workouttrackerapi.workout.dto.Workout_Exercises_Request;
import com.workouttrackerapi.workout.dto.reports.MonthlySummaryResponse;
import com.workouttrackerapi.workout.dto.reports.ProgressResponse;
import com.workouttrackerapi.workout.dto.reports.UpdateRequest;
import com.workouttrackerapi.workout.dto.reports.WorkoutHistoryResponse;

import com.workouttrackerapi.workout.enums.STATUS;
import com.workouttrackerapi.workout.model.WorkoutHistory;
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
                    "you all ready have a workout scheduled at this time " + existed_workout.getTitle()
                            + " " + existed_workout.getScheduledDate()
                            + " "
                            + existed_workout.getScheduledTime());

        }

        Workouts workouts = new Workouts();
        workouts.setTitle(wpRequest.getTitle());
        workouts.setDescription(wpRequest.getDescription());
        workouts.setScheduledDate(wpRequest.getScheduled_date());
        workouts.setScheduledTime(wpRequest.getScheduled_time());
        workouts.setUsers(user);
        workouts.setStaus(STATUS.PLANNED);

        ArrayList<WorkoutExercises> workout_Exercises_list = new ArrayList<>();
        for (Workout_Exercises_Request req : wpRequest.getWorkout_Exercises_Request()) {

            Optional<Exercises> exe = eRepository.findById(req.getId());
            if (exe.isEmpty()) {

                throw new ExerciseNotFoundException("exercise not found for id " + req.getId());
            }
            WorkoutExercises workout_Exercises = new WorkoutExercises();
            workout_Exercises.setExercises(exe.get());
            workout_Exercises.setReps(req.getReps());
            workout_Exercises.setSets(req.getSets());
            workout_Exercises.setWeight(req.getWeight());
            workout_Exercises.setWorkouts(workouts);
            workout_Exercises_list.add(workout_Exercises);
        }
        workouts.setWorkout_Exercises(workout_Exercises_list);

        Workouts savvedWorkouts = workOutRepository.save(workouts);

        List<Workout_Exercises_Request> list = new ArrayList<>();
        for (WorkoutExercises req : savvedWorkouts.getWorkout_Exercises()) {
            Workout_Exercises_Request resExe = new Workout_Exercises_Request();
            resExe.setId(req.getId());
            resExe.setReps(req.getReps());
            resExe.setSets(req.getSets());
            resExe.setWeight(req.getWeight());
            list.add(resExe);
        }

        return new WorkoutResponse(savvedWorkouts.getTitle(),
                savvedWorkouts.getDescription(),
                savvedWorkouts.getScheduledDate(),
                savvedWorkouts.getScheduledTime(),
                savvedWorkouts.getUsers().getId(),
                list);

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
                            we.setSets(req.getSets());
                        }
                        if (req.getReps() != null) {
                            we.setReps(req.getReps());
                        }
                        if (req.getWeight() != null) {
                            we.setWeight(req.getWeight());
                        }
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    WorkoutExercises workoutExercise = new WorkoutExercises();

                    workoutExercise.setExercises(exercise);
                    workoutExercise.setSets(req.getSets());
                    workoutExercise.setReps(req.getReps());
                    workoutExercise.setWeight(req.getWeight());
                    workoutExercise.setWorkouts(workouts);

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

        List<Workout_Exercises_Request> exercises = new ArrayList<>();

        for (WorkoutExercises we : workout.getWorkout_Exercises()) {

            Workout_Exercises_Request dto = new Workout_Exercises_Request();

            dto.setId(we.getExercises().getId());
            dto.setSets(we.getSets());
            dto.setReps(we.getReps());
            dto.setWeight(we.getWeight());

            exercises.add(dto);
        }

        WorkoutResponse response = new WorkoutResponse();

        response.setTitle(workout.getTitle());
        response.setDescription(workout.getDescription());
        response.setScheduledDate(workout.getScheduledDate());
        response.setScheduledTime(workout.getScheduledTime());
        response.setUserId(workout.getUsers().getId());
        response.setWorkout_Exercises_Request(exercises);

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
            throw new WorkoutNotFoundException(" workwouts not found");
        }
        Workouts wk = existedworkout.get();

        List<Workout_Exercises_Request> list = new ArrayList<>();
        for (WorkoutExercises we : wk.getWorkout_Exercises()) {
            list.add(new Workout_Exercises_Request(
                    we.getExercises().getId(),
                    we.getExercises().getName(),
                    we.getSets(),
                    we.getReps(),
                    we.getWeight()));
        }
        return new WorkoutDetaiilsResponse(wk.getTitle(), wk.getDescription(), wk.getScheduledDate(),
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

            List<Workout_Exercises_Request> exerciseList = new ArrayList<>();

            for (WorkoutExercises we : wk.getWorkout_Exercises()) {

                Workout_Exercises_Request dto = new Workout_Exercises_Request();

                dto.setName(we.getExercises().getName());
                dto.setId(we.getExercises().getId());
                dto.setSets(we.getSets());
                dto.setReps(we.getReps());
                dto.setWeight(we.getWeight());

                exerciseList.add(dto);
            }

            wd.setExercises(exerciseList);

            responselist.add(wd);
        }

        return responselist;
    }

    public WorkoutCompletionResponse completeWorkout(Long workoutId, Users user, WorkoutCompletionRequest request) {

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

        return new WorkoutCompletionResponse("Workout marked as completed", "COMPLETED");
    }

    ////////////////// ************ */ workout report
    ////////////////// service************************////////////////

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
}
