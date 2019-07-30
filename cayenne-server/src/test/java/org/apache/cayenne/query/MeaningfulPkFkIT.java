package org.apache.cayenne.query;

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.testdo.meaningful.CalendarEvent;
import org.apache.cayenne.testdo.meaningful.ContactAffliation;
import org.apache.cayenne.unit.di.server.CayenneProjects;
import org.apache.cayenne.unit.di.server.ServerCase;
import org.apache.cayenne.unit.di.server.UseServerRuntime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@UseServerRuntime(CayenneProjects.MEANINGFUL_PK_FK)
public class MeaningfulPkFkIT extends ServerCase {

    @Inject
    private DataContext context;

    @Test
    public void test() {
        ContactAffliation contactAffliation = context.newObject(ContactAffliation.class);
        contactAffliation.setCompanyId(1);
        contactAffliation.setContactId(1);
        contactAffliation.setContactAffiliationName("TestName");
        context.commitChanges();

        CalendarEvent calendarEvent = context.newObject(CalendarEvent.class);
        calendarEvent.setEventName("Test");

        ContactAffliation affiliation = ObjectSelect.query(ContactAffliation.class)
                .selectFirst(context);
        calendarEvent.setToAffiliation(affiliation);
        context.commitChanges();

        List<CalendarEvent> calendarEvents = ObjectSelect.query(CalendarEvent.class)
                .select(context);
        assertEquals(1, calendarEvents.size());
        assertEquals("Test", calendarEvents.get(0).getEventName());

        ContactAffliation contactAffliation1 = calendarEvents.get(0).getToAffiliation();
        assertNotNull(contactAffliation1);
        assertEquals((Integer)1, contactAffliation1.getCompanyId());
        assertEquals((Integer)1, contactAffliation1.getContactId());
        assertEquals("TestName", contactAffliation1.getContactAffiliationName());


        List<ContactAffliation> contactAffliations = ObjectSelect.query(ContactAffliation.class)
                .select(context);
        assertEquals(1, contactAffliations.size());
        assertEquals((Integer) 1, contactAffliations.get(0).getContactId());
        assertEquals((Integer) 1, contactAffliations.get(0).getCompanyId());

        assertNotNull(contactAffliations.get(0).getEvents().get(0));
        assertEquals("Test", contactAffliations.get(0).getEvents().get(0).getEventName());
    }

}
