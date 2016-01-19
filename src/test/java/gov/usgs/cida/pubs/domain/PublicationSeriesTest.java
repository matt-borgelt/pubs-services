package gov.usgs.cida.pubs.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.json.JSONObject;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.usgs.cida.pubs.json.View;

public class PublicationSeriesTest {

    public static String defaultPubSeriesJSON = "{\"id\":13,\"text\":\"New Video\",\"code\":\"XYZ\",\"seriesDoiName\":\"doiname is here\","
    		+ "\"onlineIssn\":\"5678-8765\",\"printIssn\":\"1234-4321\",\"publicationSubtype\":{\"id\":29},\"active\":true}";

    public static String defaultPubSeriesLookupJSON = "{\"id\":13,\"text\":\"New Video\"}";

	@Test
	public void deserializeTest() {
		try {
			PublicationSeries publicationSeries = new ObjectMapper().readValue(defaultPubSeriesJSON, PublicationSeries.class);
			assertPubSeries(13, publicationSeries);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void serializePWTest() {
		try {
		    ObjectMapper mapper = new ObjectMapper();
		    String result = mapper.writerWithView(View.PW.class)
		                          .writeValueAsString(buildAPubSeries(13));
		 
	        assertThat(new JSONObject(defaultPubSeriesJSON),
	                sameJSONObjectAs(new JSONObject(result)));
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void serializeLookupTest() {
		try {
		    ObjectMapper mapper = new ObjectMapper();
		    String result = mapper.writerWithView(View.Lookup.class)
		                          .writeValueAsString(buildAPubSeries(13));
		 
	        assertThat(new JSONObject(defaultPubSeriesLookupJSON),
	                sameJSONObjectAs(new JSONObject(result)));
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

    public static void assertPubSeries(int id, PublicationSeries pubSeries) {
        assertNotNull(pubSeries);
        assertEquals(id, pubSeries.getId().intValue());
        assertEquals("New Video", pubSeries.getText());
        assertEquals("XYZ", pubSeries.getCode());
        assertEquals("doiname is here", pubSeries.getSeriesDoiName());
        assertEquals("1234-4321", pubSeries.getPrintIssn());
        assertEquals("5678-8765", pubSeries.getOnlineIssn());
        assertTrue(pubSeries.isActive());
        assertEquals(29, pubSeries.getPublicationSubtype().getId().intValue());
    }

    public static PublicationSeries buildAPubSeries(Integer id) {
    	PublicationSeries pubSeries = new PublicationSeries();
    	pubSeries.setId(id);
     	PublicationSubtype publicationSubtype = new PublicationSubtype();
     	publicationSubtype.setId(29);
     	pubSeries.setPublicationSubtype(publicationSubtype);
     	pubSeries.setText("New Video");
 		pubSeries.setCode("XYZ");
 		pubSeries.setSeriesDoiName("doiname is here");
 		pubSeries.setPrintIssn("1234-4321");
 		pubSeries.setOnlineIssn("5678-8765");
 		pubSeries.setActive(true);
     	return pubSeries;
    }
   
}