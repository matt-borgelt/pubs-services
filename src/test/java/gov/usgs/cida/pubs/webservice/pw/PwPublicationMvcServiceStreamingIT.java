package gov.usgs.cida.pubs.webservice.pw;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import org.apache.http.entity.mime.MIME;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
@DatabaseSetup("classpath:/testData/publicationType.xml")
@DatabaseSetup("classpath:/testData/publicationSubtype.xml")
@DatabaseSetup("classpath:/testData/contributor/")
@DatabaseSetup("classpath:/testData/publicationStream.xml")
public class PwPublicationMvcServiceStreamingIT extends BaseIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getAsCsvTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication?mimeType=csv"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_CSV_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications.csv"))
			.andReturn();
	
		assertEquals(getCompareFile("stream.csv"), rtn.getResponse().getContentAsString());
	}

	@Test
	public void getAsTsvTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication?mimeType=tsv"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_TSV_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications.tsv"))
			.andReturn();
		
		final String compareFile = getCompareFile("stream.tsv");
		final String contentAsString = rtn.getResponse().getContentAsString();
	
		assertEquals(compareFile, contentAsString);
	}

	@Test
	public void getAsXlsxTest() throws Exception {
		mockMvc.perform(get("/publication?mimeType=xlsx"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_XLSX_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andExpect(header().string(MIME.CONTENT_DISPOSITION, "attachment; filename=publications.xlsx"));
		//TODO verify xlsx
		//	.andReturn();

		//assertEquals(getCompareFile("stream.xlsx"), rtn.getResponse().getContentAsString());
	}

	@Test
	public void getAsJsonTest() throws Exception {
		MvcResult rtn = mockMvc.perform(get("/publication?mimeType=json"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
			.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
			.andReturn();
	
		assertThat(new JSONObject(rtn.getResponse().getContentAsString()), sameJSONObjectAs(new JSONObject(getCompareFile("stream.json"))).allowingAnyArrayOrdering());
	}

	@Test
	public void getPwPublicationPeriodTest() throws Exception {
		//dot in index
		MvcResult rtn = mockMvc.perform(get("/publication/6.1?mimetype=json").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE))
		.andExpect(content().encoding(PubsConstantsHelper.DEFAULT_ENCODING))
		.andReturn();

		assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
				sameJSONObjectAs(new JSONObject(getCompareFile("pwPublication/indexDot.json"))).allowingAnyArrayOrdering());
	}
}
