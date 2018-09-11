package com.skushnaryov.lighttask.lighttask

object Constants {
    const val TASKS_CHANNEL_ID = "1"
    const val TASK_REMIND_CHANNEL_ID = "2"
    const val REMINDERS_CHANNELS_ID = "3"

    const val TASKS_CHANNEL_NAME = "Tasks"
    const val TASK_REMIND_CHANNEL_NAME = "Task reminders"
    const val REMINDERS_CHANNEL_NAME = "Reminders"

    const val TASK_RECIEVER = "com.skushnaryov.lighttask.TASK"
    const val TASK_DONE_RECIEVER = "com.skushnaryov.lighttask.TASK_DONE"
    const val TASK_REMIND_RECIEVER = "com.skushnaryov.lighttask.TASK_REMINDER"
    const val REMINDER_RECIEVER = "com.skushnaryov.lighttask.REMINDER"
    const val REMINDER_OFF_RECIEVER = "com.skushnaryov.lighttask.REMINDER_OFF"

    const val EXTRAS_ID = "id"
    const val EXTRAS_NAME = "name"
    const val EXTRAS_REMIND_TEXT = "text"
    const val EXTRAS_TIME_REPEAT = "repeat"

    const val CHANGE_ID = "task id"
    const val CHANGE_REMIND_ID = "task remind id"
    const val CHANGE_NAME = "task name"
    const val CHANGE_DATE = "task date"
    const val CHANGE_REMIND_DATE = "task remind date"
    const val CHANGE_CURRENT_DAY = "task current day"
    const val CHANGE_SUBTASKS = "task subtasks"
    const val CHANGE_GROUP = "task group"

    const val REMIND_MIN = "min"
    const val REMIND_HOUR = "hour"
    const val REMIND_DAY = "day"
}