package me.carda.awesome_notifications.core.managers;

import android.app.AlarmManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.databases.SQLiteSchedulesDB;
import me.carda.awesome_notifications.core.databases.SqLiteCypher;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.models.NotificationModel;
import me.carda.awesome_notifications.core.utils.StringUtils;

public class ScheduleManager {

    private static SQLiteSchedulesDB _getPrimitiveDB(Context context){
        SQLiteSchedulesDB schedulesDB = SQLiteSchedulesDB.getInstance(context);

        try {
            List<NotificationModel> oldSchedules = shared.getAllObjects(
                    context, Definitions.SHARED_SCHEDULED_NOTIFICATIONS);
            if (!oldSchedules.isEmpty()) {
                migrateSharedScheduleDatabase(context, schedulesDB, oldSchedules);
                shared.removeAll(context, Definitions.SHARED_SCHEDULED_NOTIFICATIONS);
            }
        } catch (AwesomeNotificationsException e) {
            throw new RuntimeException(e);
        }

        return schedulesDB;
    }

    private static void migrateSharedScheduleDatabase(
        Context context,
        SQLiteSchedulesDB db,
        List<NotificationModel> schedules
    ){
        for (NotificationModel schedule : schedules) {
            db.saveSchedule(
                context,
                schedule.content.id.toString(),
                schedule.content.channelKey,
                schedule.content.groupKey,
                schedule.toJson()
            );
        }
    }

    private static final RepositoryManager<NotificationModel> shared
            = new RepositoryManager<>(
            StringUtils.getInstance(),
            "ScheduleManager",
            NotificationModel.class,
            "NotificationModel");

    public static List<NotificationModel> listSchedules(
            Context context
    ) throws AwesomeNotificationsException {
        List<NotificationModel> schedules = new ArrayList<>();
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        Map<String, String> schedulesRaw = schedulesDB.getAllSchedules(context);
        for (String json :
                schedulesRaw.values()) {
            schedules.add(new NotificationModel().fromJson(json));
        }
        return schedules;
    }

    public static NotificationModel getScheduleById(
            Context context, String Id
    ) throws AwesomeNotificationsException {
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        Map<String, String> schedulesRaw = schedulesDB.getScheduleById(context, Id);
        for (String json : schedulesRaw.values()) {
            return new NotificationModel().fromJson(json);
        }
        return null;
    }

    public static List<String> listScheduledIds(Context context) {
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        Map<String, String> schedulesRaw = schedulesDB.getAllSchedules(context);
        return new ArrayList<>(schedulesRaw.keySet());
    }

    public static List<String> listScheduledIdsFromChannel(Context context, String channelKey) {
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        Map<String, String> schedulesRaw = schedulesDB.getSchedulesByChannelKey(context, channelKey);
        return new ArrayList<>(schedulesRaw.keySet());
    }

    public static List<String> listScheduledIdsFromGroup(Context context, String groupKey) {
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        Map<String, String> schedulesRaw = schedulesDB.getSchedulesByGroupKey(context, groupKey);
        return new ArrayList<>(schedulesRaw.keySet());
    }

    public static Boolean removeSchedule(
        Context context, NotificationModel notificationModel
    ) throws AwesomeNotificationsException {
        String targetId = notificationModel.content.id.toString();
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        schedulesDB.removeScheduleById(context, targetId);
        return true;
    }

    public static Boolean saveSchedule(
        Context context, NotificationModel notificationModel
    ) throws AwesomeNotificationsException {
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        schedulesDB.saveSchedule(
            context,
            notificationModel.content.id.toString(),
            notificationModel.content.channelKey,
            notificationModel.content.groupKey,
            notificationModel.toJson()
        );
        return true;
    }

    public static void cancelScheduleById(
            Context context, String id
    ) throws AwesomeNotificationsException {
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        schedulesDB.removeScheduleById(context, id);
    }

    public static void cancelSchedulesByChannelKey(
            Context context, String channelKey
    ) throws AwesomeNotificationsException {
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        schedulesDB.removeSchedulesByChannelKey(context, channelKey);
    }

    public static void cancelSchedulesByGroupKey(
            Context context, String groupKey
    ) throws AwesomeNotificationsException {
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        schedulesDB.removeSchedulesByGroupKey(context, groupKey);
    }

    public static void cancelAllSchedules(
            Context context
    ) throws AwesomeNotificationsException {
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        schedulesDB.removeAllSchedules(context);
    }

    public static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static boolean isPreciseAlarmGloballyAllowed(Context context){
        AlarmManager alarmManager = getAlarmManager(context);
        return isPreciseAlarmGloballyAllowed(alarmManager);
    }

    public static boolean isPreciseAlarmGloballyAllowed(AlarmManager alarmManager){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S /*Android 12*/)
            return alarmManager.canScheduleExactAlarms();
        return true;
    }

    public static void commitChanges(Context context) throws AwesomeNotificationsException {
        SQLiteSchedulesDB schedulesDB = _getPrimitiveDB(context);
        schedulesDB.commit(context);
    }
}
