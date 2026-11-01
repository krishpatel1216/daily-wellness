package com.example.data.db

import com.example.data.model.RoutineTask
import com.example.data.model.SplitDefinition

object WellnessDataDefaults {

    val defaultMorningTasks = listOf(
        RoutineTask(
            id = "task-1",
            category = "morning",
            title = "5:00 AM Morning Hydration Glass",
            time = "5:00 AM • 500ml",
            desc = "Warm lemon water or mineral water to kickstart metabolism.",
            isCompleted = false,
            orderIndex = 1
        ),
        RoutineTask(
            id = "task-2",
            category = "morning",
            title = "Mindfulness & Breath Meditation",
            time = "5:05 AM • 20 mins",
            desc = "Quiet sitting, mental clarity, and setting daily intent.",
            isCompleted = false,
            orderIndex = 2
        ),
        RoutineTask(
            id = "task-3",
            category = "morning",
            title = "Sunrise Yoga Flow & Surya Namaskar",
            time = "5:25 AM • 30 mins",
            desc = "Dynamic mobility, 6 rounds of Surya Namaskar.",
            isCompleted = false,
            orderIndex = 3
        )
    )

    val workoutSplits = listOf(
        SplitDefinition(
            key = "back-biceps",
            name = "Back & Biceps",
            daysLabel = "Mon & Thu",
            dayRange = "Monday & Thursday",
            defaultTasks = listOf(
                RoutineTask(
                    id = "bb-1",
                    category = "back-biceps",
                    title = "Dynamic Shoulder & Spine Warm-Up",
                    time = "8 mins",
                    desc = "Arm swings, band pull-aparts, cat-cow stretches.",
                    orderIndex = 1
                ),
                RoutineTask(
                    id = "bb-2",
                    category = "back-biceps",
                    title = "Pull-Ups / Lat Pulldown",
                    time = "4 sets • 10-12 reps",
                    desc = "Drive elbows down, squeeze lats at the bottom.",
                    orderIndex = 2
                ),
                RoutineTask(
                    id = "bb-3",
                    category = "back-biceps",
                    title = "Bent-Over Barbell / Dumbbell Row",
                    time = "4 sets • 10 reps",
                    desc = "Flat back, pull into lower abdomen for thickness.",
                    orderIndex = 3
                ),
                RoutineTask(
                    id = "bb-4",
                    category = "back-biceps",
                    title = "Incline Dumbbell Bicep Curls",
                    time = "3 sets • 12 reps",
                    desc = "Full stretch at bottom, strict curl without swinging.",
                    orderIndex = 4
                ),
                RoutineTask(
                    id = "bb-5",
                    category = "back-biceps",
                    title = "Hammer Curls & Forearm Hang",
                    time = "3 sets • 15 reps",
                    desc = "Brachialis & grip development, 30s dead hang finish.",
                    orderIndex = 5
                )
            )
        ),
        SplitDefinition(
            key = "chest-triceps",
            name = "Chest & Triceps",
            daysLabel = "Tue & Fri",
            dayRange = "Tuesday & Friday",
            defaultTasks = listOf(
                RoutineTask(
                    id = "ct-1",
                    category = "chest-triceps",
                    title = "Rotator Cuff & Chest Warm-Up",
                    time = "8 mins",
                    desc = "Scapular push-ups, band dislocates, light flyes.",
                    orderIndex = 1
                ),
                RoutineTask(
                    id = "ct-2",
                    category = "chest-triceps",
                    title = "Flat Barbell / Dumbbell Bench Press",
                    time = "4 sets • 8-10 reps",
                    desc = "Control the descent, powerful press through chest.",
                    orderIndex = 2
                ),
                RoutineTask(
                    id = "ct-3",
                    category = "chest-triceps",
                    title = "Incline Dumbbell Press",
                    time = "4 sets • 10-12 reps",
                    desc = "Upper pectoral focus, 30-degree incline.",
                    orderIndex = 3
                ),
                RoutineTask(
                    id = "ct-4",
                    category = "chest-triceps",
                    title = "Parallel Bar Dips / Cable Flyes",
                    time = "3 sets • 12-15 reps",
                    desc = "Lean slightly forward for deep chest activation.",
                    orderIndex = 4
                ),
                RoutineTask(
                    id = "ct-5",
                    category = "chest-triceps",
                    title = "Rope Tricep Pushdowns & Skullcrushers",
                    time = "4 sets • 12 reps",
                    desc = "Flared rope lockout for lateral tricep head.",
                    orderIndex = 5
                )
            )
        ),
        SplitDefinition(
            key = "legs-shoulders",
            name = "Legs & Shoulders",
            daysLabel = "Wed & Sat",
            dayRange = "Wednesday & Saturday",
            defaultTasks = listOf(
                RoutineTask(
                    id = "ls-1",
                    category = "legs-shoulders",
                    title = "Hip Mobility & Ankle Warm-Up",
                    time = "8 mins",
                    desc = "Deep goblet squat hold, leg swings, hip circles.",
                    orderIndex = 1
                ),
                RoutineTask(
                    id = "ls-2",
                    category = "legs-shoulders",
                    title = "Barbell Back Squats / Leg Press",
                    time = "4 sets • 8-10 reps",
                    desc = "Hit parallel depth, drive through mid-foot.",
                    orderIndex = 2
                ),
                RoutineTask(
                    id = "ls-3",
                    category = "legs-shoulders",
                    title = "Romanian Deadlifts (Hamstrings)",
                    time = "4 sets • 10 reps",
                    desc = "Hinge hips backward, keep lats engaged.",
                    orderIndex = 3
                ),
                RoutineTask(
                    id = "ls-4",
                    category = "legs-shoulders",
                    title = "Overhead Dumbbell Shoulder Press",
                    time = "4 sets • 10 reps",
                    desc = "Core braced, press straight overhead.",
                    orderIndex = 4
                ),
                RoutineTask(
                    id = "ls-5",
                    category = "legs-shoulders",
                    title = "Lateral Raises & Face Pulls",
                    time = "4 sets • 15 reps",
                    desc = "Side deltoid width & rear delt posture protector.",
                    orderIndex = 5
                )
            )
        ),
        SplitDefinition(
            key = "sunday-rest",
            name = "Active Recovery",
            daysLabel = "Sunday",
            dayRange = "Sunday Active Recovery",
            defaultTasks = listOf(
                RoutineTask(
                    id = "sr-1",
                    category = "sunday-rest",
                    title = "Deep Hip & Spine Mobility Flow",
                    time = "20 mins",
                    desc = "Pigeon pose, child pose, spinal twists.",
                    orderIndex = 1
                ),
                RoutineTask(
                    id = "sr-2",
                    category = "sunday-rest",
                    title = "Light 30-Min Outdoor Nature Walk",
                    time = "30 mins",
                    desc = "Zone 1 low-heart-rate recovery & fresh air.",
                    orderIndex = 2
                ),
                RoutineTask(
                    id = "sr-3",
                    category = "sunday-rest",
                    title = "Full Body Foam Rolling & Hydration",
                    time = "15 mins",
                    desc = "Release tight fascia in quads, back, and calves.",
                    orderIndex = 3
                )
            )
        )
    )

    fun getAllInitialTasks(): List<RoutineTask> {
        val list = mutableListOf<RoutineTask>()
        list.addAll(defaultMorningTasks)
        workoutSplits.forEach { split ->
            list.addAll(split.defaultTasks)
        }
        return list
    }
}
