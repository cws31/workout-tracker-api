package com.workouttrackerapi.services;

import org.springframework.stereotype.Service;
import java.util.*;

import com.workouttrackerapi.dtos.UpdateRequest;
import com.workouttrackerapi.dtos.WorkoutDetaiilsResponse;
import com.workouttrackerapi.dtos.WorkoutRequest;
import com.workouttrackerapi.dtos.WorkoutResponse;
import com.workouttrackerapi.dtos.Workout_Exercises_Request;
import com.workouttrackerapi.enums.STATUS;
import com.workouttrackerapi.exceptions.ExerciseNotFoundException;
import com.workouttrackerapi.exceptions.WorkoutSlotBookedException;
import com.workouttrackerapi.models.Exercises;
import com.workouttrackerapi.models.Users;
import com.workouttrackerapi.models.Workout_Exercises;
import com.workouttrackerapi.models.Workouts;
import com.workouttrackerapi.repositories.ExerciseRepository;
import com.workouttrackerapi.repositories.WorkOutRepository;

import jakarta.transaction.Transactional;

@Service
public class WorkoutService {

    private final WorkOutRepository workOutRepository;
    private final ExerciseRepository eRepository;

    public WorkoutService(WorkOutRepository workOutRepository, ExerciseRepository eRepository) {
        this.workOutRepository = workOutRepository;
        this.eRepository = eRepository;
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
        workouts.setStaus(STATUS.PENDDING);

        ArrayList<Workout_Exercises> workout_Exercises_list = new ArrayList<>();
        for (Workout_Exercises_Request req : wpRequest.getWorkout_Exercises_Request()) {

            Optional<Exercises> exe = eRepository.findById(req.getId());
            if (exe.isEmpty()) {

                throw new ExerciseNotFoundException("exercise not found for id " + req.getId());
            }
            Workout_Exercises workout_Exercises = new Workout_Exercises();
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
        for (Workout_Exercises req : savvedWorkouts.getWorkout_Exercises()) {
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
                for (Workout_Exercises we : workouts.getWorkout_Exercises()) {
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
                    Workout_Exercises workoutExercise = new Workout_Exercises();

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

        for (Workout_Exercises we : workout.getWorkout_Exercises()) {

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
            throw new ExerciseNotFoundException("workout not found");
        }
        workOutRepository.delete(existedWorkout.get());

    }

    public WorkoutDetaiilsResponse getWorkoutDetails(Users user, Long workoutid) {
        Long userid = user.getId();
        Optional<Workouts> existedworkout = workOutRepository.findByIdAndUsersId(workoutid, userid);

        if (existedworkout.isEmpty()) {
            throw new ExerciseNotFoundException(" workwouts not found");
        }
        Workouts wk = existedworkout.get();

        List<Workout_Exercises_Request> list = new ArrayList<>();
        for (Workout_Exercises we : wk.getWorkout_Exercises()) {
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

}
