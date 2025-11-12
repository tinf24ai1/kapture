package com.kapture.kapture.reminder

/** Liefert die plattformspezifische Scheduler-Implementation. */
expect fun createReminderScheduler(): ReminderScheduler
