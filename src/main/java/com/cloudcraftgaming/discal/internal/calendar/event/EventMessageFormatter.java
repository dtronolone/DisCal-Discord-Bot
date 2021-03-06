package com.cloudcraftgaming.discal.internal.calendar.event;

import com.cloudcraftgaming.discal.Main;
import com.cloudcraftgaming.discal.database.DatabaseManager;
import com.cloudcraftgaming.discal.internal.calendar.CalendarAuth;
import com.cloudcraftgaming.discal.internal.data.CalendarData;
import com.cloudcraftgaming.discal.internal.data.GuildSettings;
import com.cloudcraftgaming.discal.utils.EventColor;
import com.cloudcraftgaming.discal.utils.MessageManager;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

import javax.annotation.Nullable;

/**
 * Created by Nova Fox on 1/3/2017.
 * Website: www.cloudcraftgaming.com
 * For Project: DisCal
 */
@SuppressWarnings("Duplicates")
public class EventMessageFormatter {

    /**
     * Gets an EmbedObject for the specified event.
     * @param event The event involved.
     * @param settings The guild's settings
     * @return The EmbedObject of the event.
     */
    public static EmbedObject getEventEmbed(Event event, GuildSettings settings) {
        EmbedBuilder em = new EmbedBuilder();
        em.withAuthorIcon(Main.client.getGuildByID(266063520112574464L).getIconURL());
        em.withAuthorName("DisCal");
        em.withTitle(MessageManager.getMessage("Embed.Event.Info.Title", settings));
        if (event.getSummary() != null) {
            em.appendField(MessageManager.getMessage("Embed.Event.Info.Summary", settings), event.getSummary(), true);
        }
        if (event.getDescription() != null) {
            em.appendField(MessageManager.getMessage("Embed.Event.Info.Description", settings), event.getDescription(), true);
        }
        em.appendField(MessageManager.getMessage("Embed.Event.Info.StartDate", settings), getHumanReadableDate(event.getStart()), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Info.StartTime", settings), getHumanReadableTime(event.getStart()), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Info.EndDate", settings), getHumanReadableDate(event.getEnd()), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Info.EndTime", settings), getHumanReadableTime(event.getEnd()), true);

        try {
            //TODO: add support for multiple calendars...
            CalendarData data = DatabaseManager.getManager().getMainCalendar(settings.getGuildID());
            Calendar service = CalendarAuth.getCalendarService();
            String tz = service.calendars().get(data.getCalendarAddress()).execute().getTimeZone();
            em.appendField(MessageManager.getMessage("Embed.Event.Info.TimeZone", settings), tz, true);
        } catch (Exception e1) {
            em.appendField(MessageManager.getMessage("Embed.Event.Info.TimeZone", settings), "Error/Unknown", true);
        }
        //TODO: Add info on recurrence here.
        em.withUrl(event.getHtmlLink());
        em.withFooterText(MessageManager.getMessage("Embed.Event.Info.ID", "%id%", event.getId(), settings));
        try {
            EventColor ec = EventColor.fromId(Integer.valueOf(event.getColorId()));
            em.withColor(ec.getR(), ec.getG(), ec.getB());
        } catch (Exception e) {
            //Color is null, ignore and add our default.
            em.withColor(56, 138, 237);
        }

        return em.build();
    }

    /**
     * Gets an EmbedObject for the specified event.
     * @param event The event involved.
     * @return The EmbedObject of the event.
     */
    public static EmbedObject getCondensedEventEmbed(Event event, GuildSettings settings) {
        EmbedBuilder em = new EmbedBuilder();
        em.withAuthorIcon(Main.client.getGuildByID(266063520112574464L).getIconURL());
        em.withAuthorName("DisCal");
        em.withTitle(MessageManager.getMessage("Embed.Event.Condensed.Title", settings));
        if (event.getSummary() != null) {
            em.appendField(MessageManager.getMessage("Embed.Event.Condensed.Summary", settings), event.getSummary(), true);
        }
        em.appendField(MessageManager.getMessage("Embed.Event.Condensed.Date", settings), getHumanReadableDate(event.getStart()), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Condensed.ID", settings), event.getId(), true);
        em.withUrl(event.getHtmlLink());
        try {
            EventColor ec = EventColor.fromId(Integer.valueOf(event.getColorId()));
            em.withColor(ec.getR(), ec.getG(), ec.getB());
        } catch (Exception e) {
            //Color is null, ignore and add our default.
            em.withColor(56, 138, 237);
        }

        return em.build();
    }

    /**
     * Gets an EmbedObject for the specified PreEvent.
     * @param event The PreEvent to get an embed for.
     * @return The EmbedObject of the PreEvent.
     */
    public static EmbedObject getPreEventEmbed(PreEvent event, GuildSettings settings) {
        EmbedBuilder em = new EmbedBuilder();
        em.withAuthorIcon(Main.client.getGuildByID(266063520112574464L).getIconURL());
        em.withAuthorName("DisCal");
        em.withTitle(MessageManager.getMessage("Embed.Event.Pre.Title", settings));
        if (event.isEditing()) {
            em.appendField(MessageManager.getMessage("Embed.Event.Pre.Id", settings), event.getEventId(), true);
        }
        if (event.getSummary() != null) {
            em.appendField(MessageManager.getMessage("Embed.Event.Pre.Summary", settings), event.getSummary(), true);
        } else {
        	em.appendField(MessageManager.getMessage("Embed.Event.Pre.Summary", settings), "NOT SET", true);
		}
        if (event.getDescription() != null) {
            em.appendField(MessageManager.getMessage("Embed.Event.Pre.Description", settings), event.getDescription(), true);
        } else {
        	em.appendField(MessageManager.getMessage("Embed.Event.Pre.Description", settings), "NOT SET", true);
		}
        if (event.shouldRecur()) {
            em.appendField(MessageManager.getMessage("Embed.Event.Pre.Recurrence", settings), event.getRecurrence().toHumanReadable(), true);
        } else {
            em.appendField(MessageManager.getMessage("Embed.Event.Pre.Recurrence", settings), "N/a", true);
        }
        em.appendField(MessageManager.getMessage("Embed.Event.Pre.StartDate", settings), getHumanReadableDate(event.getViewableStartDate()), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Pre.StartTime", settings), EventMessageFormatter.getHumanReadableTime(event.getViewableStartDate()), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Pre.EndDate", settings), getHumanReadableDate(event.getViewableEndDate()), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Pre.EndTime", settings), EventMessageFormatter.getHumanReadableTime(event.getViewableEndDate()), true);
        em.appendField(MessageManager.getMessage("Embed.Event.Pre.TimeZone", settings), event.getTimeZone(), true);
        //TODO: Add info on recurrence here.

        em.withFooterText(MessageManager.getMessage("Embed.Event.Pre.Key", settings));
        EventColor ec = event.getColor();
        em.withColor(ec.getR(), ec.getG(), ec.getB());

        return em.build();
    }

    /**
     * Gets an EmbedObject for the specified CreatorResponse.
     * @param ecr The CreatorResponse involved.
     * @return The EmbedObject for the CreatorResponse.
     */
    public static EmbedObject getEventConfirmationEmbed(EventCreatorResponse ecr, GuildSettings settings) {
        EmbedBuilder em = new EmbedBuilder();
        em.withAuthorIcon(Main.client.getGuildByID(266063520112574464L).getIconURL());
        em.withAuthorName("DisCal");
        em.withTitle(MessageManager.getMessage("Embed.Event.Confirm.Title", settings));
        em.appendField(MessageManager.getMessage("Embed.Event.Confirm.ID", settings), ecr.getEvent().getId(), false);
        em.appendField(MessageManager.getMessage("Embed.Event.Confirm.Date", settings), getHumanReadableDate(ecr.getEvent().getStart()), false);
        em.withFooterText(MessageManager.getMessage("Embed.Event.Confirm.Footer", settings));
        em.withUrl(ecr.getEvent().getHtmlLink());
        try {
            EventColor ec = EventColor.fromId(Integer.valueOf(ecr.getEvent().getColorId()));
            em.withColor(ec.getR(), ec.getG(), ec.getB());
        } catch (Exception e) {
            //Color is null, ignore and add our default.
            em.withColor(56, 138, 237);
        }

        return em.build();
    }

    /**
     *  Gets a formatted date.
     * @param eventDateTime The object to get the date from.
     * @return A formatted date.
     */
    public static String getHumanReadableDate(@Nullable EventDateTime eventDateTime) {
        if (eventDateTime == null) {
            return "NOT SET";
        } else {
            if (eventDateTime.getDateTime() != null) {
                String[] dateArray = eventDateTime.getDateTime().toStringRfc3339().split("-");
                String year = dateArray[0];
                String month = dateArray[1];
                String day = dateArray[2].substring(0, 2);

                return year + "/" + month + "/" + day;
            } else {
                String[] dateArray = eventDateTime.getDate().toStringRfc3339().split("-");
                String year = dateArray[0];
                String month = dateArray[1];
                String day = dateArray[2].substring(0, 2);

                return year + "/" + month + "/" + day;
            }
        }
    }

    /**
     * Gets a formatted time.
     * @param eventDateTime The object to get the time from.
     * @return A formatted time.
     */
    public static String getHumanReadableTime(@Nullable EventDateTime eventDateTime) {
        if (eventDateTime == null) {
            return "NOT SET";
        } else {
            if (eventDateTime.getDateTime() != null) {
                String[] timeArray = eventDateTime.getDateTime().toStringRfc3339().split(":");
                String suffix = "";
                String hour = timeArray[0].substring(11, 13);

                //Convert hour from 24 to 12...
                try {
                    Integer hRaw = Integer.valueOf(hour);
                    if (hRaw > 12) {
                        hour = String.valueOf(hRaw - 12);
                        suffix = "PM";
                    } else {
                        suffix = "AM";
                    }
                } catch (NumberFormatException e) {
                    //I Dunno... just should catch the error now and not crash anything...
                }

                String minute = timeArray[1];

                return hour + ":" + minute + suffix;
            } else {
                String[] timeArray = eventDateTime.getDate().toStringRfc3339().split(":");
                String suffix = "";
                String hour = timeArray[0].substring(11, 13);

                //Convert hour from 24 to 12...
                try {
                    Integer hRaw = Integer.valueOf(hour);
                    if (hRaw > 12) {
                        hour = String.valueOf(hRaw - 12);
                        suffix = "PM";
                    } else {
                        suffix = "AM";
                    }
                } catch (NumberFormatException e) {
                    //I Dunno... just should catch the error now and not crash anything...
                }

                String minute = timeArray[1];

                return hour + ":" + minute + suffix;
            }
        }
    }
}