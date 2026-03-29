package com.workouttrackerapi.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import com.workouttrackerapi.auth.model.Users;
import com.workouttrackerapi.exercise.model.Exercises;
import com.workouttrackerapi.exercise.repository.ExerciseRepository;
import com.workouttrackerapi.workout.dto.WorkoutCompletionRequest;
import com.workouttrackerapi.workout.dto.WorkoutCompletionResponse;
import com.workouttrackerapi.workout.dto.WorkoutRequest;
import com.workouttrackerapi.workout.dto.WorkoutResponse;
import com.workouttrackerapi.workout.dto.Workout_Exercises_Request;
import com.workouttrackerapi.workout.dto.reports.UpdateRequest;
import com.workouttrackerapi.workout.enums.STATUS;
import com.workouttrackerapi.workout.model.WorkoutExercises;
import com.workouttrackerapi.workout.model.WorkoutHistory;
import com.workouttrackerapi.workout.model.Workouts;
import com.workouttrackerapi.workout.repository.WorkOutRepository;
import com.workouttrackerapi.workout.repository.WorkoutExerciseRepository;
import com.workouttrackerapi.workout.repository.WorkoutHistoryRepository;
import com.workouttrackerapi.workout.service.WorkoutService;

@ExtendWith(MockitoExtension.class)
public class WorkoutServiceTest {

    @Mock
    private WorkOutRepository workOutRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private WorkoutHistoryRepository workoutHistoryRepository;

    @Mock
    private WorkoutExerciseRepository workoutExerciseRepository;

    @InjectMocks
    private WorkoutService workoutService;

    private Users users;

    @BeforeEach
    void setup() {
        users = new Users();
        users.setId(1L);
    }

    @Test
    void createWorkOut_Success() {
        Workout_Exercises_Request exReq = new Workout_Exercises_Request();
        exReq.setId(10L);
        exReq.setReps(10L);
        exReq.setSets(10L);
        exReq.setWeight(50.0);

        WorkoutRequest request = new WorkoutRequest();
        request.setTitle("Leg Day");
        request.setDescription("today i work on my leg ");
        request.setScheduled_date(Date.valueOf("2026-03-29"));
        request.setScheduled_time(Time.valueOf("10:30:00"));
        request.setWorkout_Exercises_Request(List.of(exReq));

        when(workOutRepository.findByUsersIdAndScheduledDateAndScheduledTime(anyLong(), any(), any())).thenReturn(null);

        Exercises exercises = new Exercises();
        exercises.setId(10L);
        exercises.setName("Sqats");
        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(exercises));

        when(workOutRepository.save(any())).thenAnswer(invoke -> invoke.getArgument(0));

        WorkoutResponse response = workoutService.createWorkOut(request, users);

        assertNotNull(response);
        assertEquals("Leg Day", response.getTitle());
        verify(workOutRepository, times(1)).save(any());

    }

    @Test
    void workoutSlot_full_try_at_other_time() {
        WorkoutRequest request = new WorkoutRequest();
        request.setScheduled_date(Date.valueOf("2026-03-29"));
        request.setScheduled_time(Time.valueOf("03:30:00"));

        Workouts workouts = new Workouts();
        workouts.setTitle("existing workout");

        when(workOutRepository.findByUsersIdAndScheduledDateAndScheduledTime(anyLong(), any(), any()))
                .thenReturn(workouts);

        assertThrows(RuntimeException.class, () -> {
            workoutService.createWorkOut(request, users);
        });
    }

    @Test
    void test_exercise_not_found() {
        Workout_Exercises_Request wer = new Workout_Exercises_Request();
        wer.setId(50L);

        WorkoutRequest request = new WorkoutRequest();
        request.setWorkout_Exercises_Request(List.of(wer));

        when(workOutRepository.findByUsersIdAndScheduledDateAndScheduledTime(anyLong(), any(), any())).thenReturn(null);

        when(exerciseRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            workoutService.createWorkOut(request, users);
        });
    }

    @Test
    void testGetProgress_Success() {

        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[] { 100.0, 12.0, 5L });

        when(workoutExerciseRepository.getExerciseProgress(
                anyLong(), anyLong(), any()))
                .thenReturn(mockResult);

        var response = workoutService.getProgress(1L, users);

        assertEquals(100.0, response.getMaxWeight());
        assertEquals(12.0, response.getAvgReps());
        assertEquals(5L, response.getTotalSessions());
    }

    @Test
    void testGetProgress_Empty() {

        when(workoutExerciseRepository.getExerciseProgress(
                anyLong(), anyLong(), any()))
                .thenReturn(Collections.emptyList());

        var response = workoutService.getProgress(1L, users);

        assertEquals(0.0, response.getMaxWeight());
    }

    @Test
    void testUpdateWorkoutSuccess() {

        Long workoutId = 1L;

        Workouts workout = new Workouts();
        workout.setId(workoutId);
        workout.setUsers(users);
        workout.setWorkout_Exercises(new ArrayList<>());

        when(workOutRepository.findById(workoutId))
                .thenReturn(Optional.of(workout));

        UpdateRequest request = new UpdateRequest();

        Date newDate = Date.valueOf("2026-03-30");
        Time newTime = Time.valueOf("12:00:00");

        request.setChangeScheduedDate(newDate);
        request.setChangeScheduedTime(newTime);

        Workout_Exercises_Request exReq = new Workout_Exercises_Request();
        exReq.setId(1L);
        exReq.setSets(4L);
        exReq.setReps(12L);
        exReq.setWeight(60.0);

        request.setAddExecise(List.of(exReq));

        Exercises exercise = new Exercises();
        exercise.setId(1L);

        when(exerciseRepository.findById(1L))
                .thenReturn(Optional.of(exercise));

        when(workOutRepository.save(any(Workouts.class)))
                .thenReturn(workout);

        WorkoutResponse response = workoutService.updateWorkout(workoutId, request, users);

        assertNotNull(response);
        verify(workOutRepository, times(1)).save(workout);
    }

    @Test
    void testUpdateWorkoutNotFound() {

        when(workOutRepository.findById(1L))
                .thenReturn(Optional.empty());

        UpdateRequest request = new UpdateRequest();

        assertThrows(RuntimeException.class, () -> {
            workoutService.updateWorkout(1L, request, users);
        });
    }

    @Test
    void testDeleteWorkoutSuccess() {

        Workouts workout = new Workouts();

        when(workOutRepository.findByIdAndUsersId(1L, 1L))
                .thenReturn(Optional.of(workout));

        workoutService.deleteWorkout(users, 1L);

        verify(workOutRepository, times(1)).delete(workout);
    }

    @Test
    void testDeleteWorkoutNotFound() {

        when(workOutRepository.findByIdAndUsersId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            workoutService.deleteWorkout(users, 1L);
        });
    }

    @Test
    void testCompleteWorkoutSuccess() {

        Long workoutId = 1L;

        Workouts workout = new Workouts();
        workout.setId(workoutId);
        workout.setUsers(users);
        workout.setStaus(STATUS.PLANNED);

        when(workOutRepository.findById(workoutId))
                .thenReturn(Optional.of(workout));

        WorkoutCompletionRequest request = new WorkoutCompletionRequest();
        request.setDuration(60);
        request.setNotes("Good session");

        WorkoutCompletionResponse response = workoutService.completeWorkout(workoutId, users, request);

        assertNotNull(response);
        assertEquals("COMPLETED", response.getStatus());

        verify(workOutRepository, times(1)).save(workout);
        verify(workoutHistoryRepository, times(1)).save(any());
    }

    @Test
    void testCompleteWorkoutUnauthorized() {

        Users anotherUser = new Users();
        anotherUser.setId(2L);

        Workouts workout = new Workouts();
        workout.setId(1L);
        workout.setUsers(anotherUser);

        when(workOutRepository.findById(1L))
                .thenReturn(Optional.of(workout));

        WorkoutCompletionRequest request = new WorkoutCompletionRequest();

        assertThrows(RuntimeException.class, () -> {
            workoutService.completeWorkout(1L, users, request);
        });
    }

    @Test
    void testCompleteWorkoutAlreadyCompleted() {

        Workouts workout = new Workouts();
        workout.setId(1L);
        workout.setUsers(users);
        workout.setStaus(STATUS.COMPLETED);

        when(workOutRepository.findById(1L))
                .thenReturn(Optional.of(workout));

        WorkoutCompletionRequest request = new WorkoutCompletionRequest();

        assertThrows(RuntimeException.class, () -> {
            workoutService.completeWorkout(1L, users, request);
        });
    }

    @Test
    void testGetScheduledWorkouts_Success() {

        Workouts workout = new Workouts();
        workout.setTitle("Leg Day");
        workout.setDescription("desc");
        workout.setUsers(users);
        workout.setWorkout_Exercises(new ArrayList<>());

        Exercises ex = new Exercises();
        ex.setId(1L);
        ex.setName("Squats");

        WorkoutExercises we = new WorkoutExercises();
        we.setExercises(ex);
        we.setSets(3L);
        we.setReps(10L);
        we.setWeight(50.0);

        workout.getWorkout_Exercises().add(we);

        when(workOutRepository.findByUsersIdAndStaus(anyLong(), any(), any()))
                .thenReturn(List.of(workout));

        var response = workoutService.getScheduledWorkouts(users, STATUS.PLANNED);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testGetWorkoutDetails_Success() {

        Workouts workout = new Workouts();
        workout.setUsers(users);
        workout.setWorkout_Exercises(new ArrayList<>());

        Exercises ex = new Exercises();
        ex.setId(1L);
        ex.setName("Pushups");

        WorkoutExercises we = new WorkoutExercises();
        we.setExercises(ex);
        we.setSets(3L);
        we.setReps(15L);
        we.setWeight(0.0);

        workout.getWorkout_Exercises().add(we);

        when(workOutRepository.findByIdAndUsersId(1L, 1L))
                .thenReturn(Optional.of(workout));

        var response = workoutService.getWorkoutDetails(users, 1L);

        assertNotNull(response);
    }

    @Test
    void testGetWorkoutDetails_NotFound() {

        when(workOutRepository.findByIdAndUsersId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            workoutService.getWorkoutDetails(users, 1L);
        });
    }

    @Test
    void testGetWorkoutHistory() {

        WorkoutHistory history = new WorkoutHistory();

        Workouts workout = new Workouts();
        workout.setTitle("Leg Day");

        history.setWorkout(workout);
        history.setCompletionDate(LocalDate.now());
        history.setDuration(60);
        history.setNotes("Good");

        when(workoutHistoryRepository.findByWorkout_Users_Id(anyLong()))
                .thenReturn(List.of(history));

        var response = workoutService.getWorkoutHistory(users);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testGetMonthlySummary_Success() {

        List<Object[]> result = new ArrayList<>();
        result.add(new Object[] { 5L, 300 });

        when(workoutHistoryRepository.getMonthlySummary(anyLong(), anyInt(), anyInt()))
                .thenReturn(result);

        var response = workoutService.getMonthlySummary(3, 2026, users);

        assertEquals(5L, response.getTotalWorkouts());
        assertEquals(300, response.getTotalDuration());
    }

    @Test
    void testGetMonthlySummary_Empty() {

        when(workoutHistoryRepository.getMonthlySummary(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        var response = workoutService.getMonthlySummary(3, 2026, users);

        assertEquals(0L, response.getTotalWorkouts());
    }
}
