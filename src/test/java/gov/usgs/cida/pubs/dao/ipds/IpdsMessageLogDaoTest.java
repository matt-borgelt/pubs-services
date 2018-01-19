package gov.usgs.cida.pubs.dao.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubMap;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class IpdsMessageLogDaoTest extends BaseSpringTest {

	@Resource(name="feedXml")
	public String feedXml;

	private static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors");

	@Test
	public void addGetByIdUpdate() {
		IpdsMessageLog ipdsMessageLog = new IpdsMessageLog();
		ipdsMessageLog.setMessageText("<root><node>123</node></root>");
		ipdsMessageLog.setProcessType(ProcessType.SPN_PRODUCTION);
		IpdsMessageLog persisted = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(ipdsMessageLog));
		assertEquals(ipdsMessageLog.getMessageText(), persisted.getMessageText());
		assertNotNull(persisted.getId());
		assertEquals(ProcessType.SPN_PRODUCTION, persisted.getProcessType());
		assertNull(persisted.getProcessingDetails());
		assertNull(persisted.getProdId());

		ipdsMessageLog.setMessageText("<root><node>456</node></root>");
		persisted.setProcessingDetails("It worked!!");
		persisted.setProdId(123);
		IpdsMessageLog.getDao().update(persisted);
		IpdsMessageLog updated = IpdsMessageLog.getDao().getById(persisted.getId());
		assertDaoTestResults(IpdsMessageLog.class, updated, persisted, IGNORE_PROPERTIES, true, true);

		IpdsMessageLog.getDao().add(new IpdsMessageLog());
		List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
		assertNotNull(logs);
		assertEquals(2, logs.size());
	}

	@Test
	public void getFromIpds() {
		IpdsMessageLog ipdsMessageLogSeries = new IpdsMessageLog();
		String ipdsMessage = feedXml;
		ipdsMessageLogSeries.setMessageText(ipdsMessage);
		List<PubMap> pubMaps = IpdsMessageLog.getDao().getFromIpds(IpdsMessageLog.getDao().add(ipdsMessageLogSeries));
		assertNotNull(pubMaps);
		assertEquals(37, pubMaps.size());
		assertEquals(IpdsMessageLog.IPDS_LOG_PROPERTIES.size(), pubMaps.get(0).keySet().size());
		for (String property : IpdsMessageLog.IPDS_LOG_PROPERTIES) {
			assertTrue(pubMaps.get(0).containsKey(property));
		}

		for (PubMap pub : pubMaps) {
			if (pub.get(IpdsMessageLog.IPDS_INTERNAL_ID).toString().contentEquals("81817")) {
				assertEquals("My Abstract", pub.get(IpdsMessageLog.ABSTRACT));
				assertEquals("Eddy-Miller, C.A., Bartos, T.T., and Taylor, M.L., 2013, Pesticides in Wyoming Groundwater, 2008–10: U.S. Geological Survey Scientific Investigations Report 2013–5064, 45 p.", pub.get(IpdsMessageLog.CITATION));
				assertEquals(BigDecimal.valueOf(52), pub.get(IpdsMessageLog.COSTCENTER));
				assertEquals("Wyoming Department of Agriculture", pub.get(IpdsMessageLog.COOPERATORS));
				assertEquals("XXX", pub.get(IpdsMessageLog.DIGITALOBJECTIDENTIFIER));
				assertEquals("12", pub.get(IpdsMessageLog.EDITIONNUMBER));
				assertEquals("Pesticides in Wyoming Groundwater, 2008–10", pub.get(IpdsMessageLog.FINALTITLE));
				assertEquals(BigDecimal.valueOf(81817), pub.get(IpdsMessageLog.IPDS_INTERNAL_ID));
				assertEquals("IP-035570", pub.get(IpdsMessageLog.IPNUMBER));
				assertEquals("c", pub.get(IpdsMessageLog.ISSUE));
				assertEquals("Larger Work Title", pub.get(IpdsMessageLog.JOURNALTITLE));
				assertEquals("U.S. Geological Survey", pub.get(IpdsMessageLog.NONUSGSPUBLISHER));
				assertEquals("18-98", pub.get(IpdsMessageLog.PAGERANGE));
				assertEquals("Physical Description", pub.get(IpdsMessageLog.PHYSICALDESCRIPTION));
				assertEquals("A synthesis of pesticides in groundwater data collected from 1995 through 2010 in Wyoming, examining changes in use and trends in concentrations", pub.get(IpdsMessageLog.PRODUCTSUMMARY));
				assertEquals("USGS series", pub.get(IpdsMessageLog.PRODUCTTYPEVALUE));
				assertEquals("http://pubs.usgs.gov/sir/2013/5064/, http://pubs.usgs.gov/sir/2013/5064/", pub.get(IpdsMessageLog.PUBLISHEDURL));
				assertEquals(BigDecimal.valueOf(4), pub.get(IpdsMessageLog.PUBLISHINGSERVICECENTER));
				assertEquals("Dissemination", pub.get(IpdsMessageLog.TASK));
				assertEquals("q", pub.get(IpdsMessageLog.USGSSERIESLETTER));
				assertEquals("2013-5064", pub.get(IpdsMessageLog.USGSSERIESNUMBER));
				assertEquals("Scientific Investigations Report", pub.get(IpdsMessageLog.USGSSERIESTYPEVALUE));
				assertEquals("A", pub.get(IpdsMessageLog.VOLUME));
				assertEquals("Pesticides in groundwater of Wyoming, 2008-2010", pub.get(IpdsMessageLog.WORKINGTITLE));
			}
		}
	}

	public static PubMap createPubMap1() {
		PubMap pubMap = new PubMap();
		pubMap.put(IpdsMessageLog.PRODUCTTYPEVALUE, "USGS series");
		pubMap.put(IpdsMessageLog.USGSSERIESTYPEVALUE, "Open-File Report");//"Journal of Crustacean Biology")480;
		pubMap.put(IpdsMessageLog.USGSSERIESNUMBER, "12.1");
		pubMap.put(IpdsMessageLog.USGSSERIESLETTER, "a");
		pubMap.put(IpdsMessageLog.FINALTITLE, "My Final Title");
		pubMap.put(IpdsMessageLog.WORKINGTITLE, "My Working Title");
		pubMap.put(IpdsMessageLog.ABSTRACT, "My Abstract");
		pubMap.put(IpdsMessageLog.NONUSGSPUBLISHER, "Not one of those USGS Publishers");
		pubMap.put(IpdsMessageLog.DIGITALOBJECTIDENTIFIER, "doi");
		pubMap.put(IpdsMessageLog.COOPERATORS, "I really want to cooperate");
		pubMap.put(IpdsMessageLog.CITATION, "A short citation");
		pubMap.put(IpdsMessageLog.PHYSICALDESCRIPTION, "physical desc");
		pubMap.put(IpdsMessageLog.PAGERANGE, "pages 1-5");
		pubMap.put(IpdsMessageLog.PRODUCTSUMMARY, "what a summary");
		pubMap.put(IpdsMessageLog.IPNUMBER, "IP1234");
		pubMap.put(IpdsMessageLog.TASK, ProcessType.SPN_PRODUCTION.getIpdsValue());
		pubMap.put(IpdsMessageLog.IPDS_INTERNAL_ID, 453228);
		pubMap.put(IpdsMessageLog.JOURNALTITLE, "A Journal");
		pubMap.put(IpdsMessageLog.VOLUME, "V1");
		pubMap.put(IpdsMessageLog.ISSUE, "I1");
		pubMap.put(IpdsMessageLog.EDITIONNUMBER, "E1");
		pubMap.put(IpdsMessageLog.PUBLISHINGSERVICECENTER, "2");

		return pubMap;
	}

	public static PubMap createPubMap2() {
		PubMap pubMap = new PubMap();
		pubMap.put(IpdsMessageLog.PRODUCTTYPEVALUE, "New");
		pubMap.put(IpdsMessageLog.USGSSERIESTYPEVALUE, "Series Value");//"Journal of Crustacean Biology")480;
		pubMap.put(IpdsMessageLog.USGSSERIESNUMBER, ".");
		pubMap.put(IpdsMessageLog.USGSSERIESLETTER, "a");
		pubMap.put(IpdsMessageLog.WORKINGTITLE, "My Working Title");
		pubMap.put(IpdsMessageLog.ABSTRACT, "My Abstract");
		pubMap.put(IpdsMessageLog.NONUSGSPUBLISHER, "Not one of those USGS Publishers");
		pubMap.put(IpdsMessageLog.DIGITALOBJECTIDENTIFIER, "doi");
		pubMap.put(IpdsMessageLog.COOPERATORS, "I really want to cooperate");
		pubMap.put(IpdsMessageLog.CITATION, "A short citation");
		pubMap.put(IpdsMessageLog.PHYSICALDESCRIPTION, "physical desc");
		pubMap.put(IpdsMessageLog.PAGERANGE, "pages 1-5");
		pubMap.put(IpdsMessageLog.PRODUCTSUMMARY, "what a summary");
		pubMap.put(IpdsMessageLog.IPNUMBER, "IP1234");
		pubMap.put(IpdsMessageLog.TASK, ProcessType.SPN_PRODUCTION.getIpdsValue());
		pubMap.put(IpdsMessageLog.IPDS_INTERNAL_ID, 453228);
		pubMap.put(IpdsMessageLog.JOURNALTITLE, "A Journal Title");
		pubMap.put(IpdsMessageLog.VOLUME, "V2");
		pubMap.put(IpdsMessageLog.ISSUE, "I2");
		pubMap.put(IpdsMessageLog.EDITIONNUMBER, "E2");
		pubMap.put(IpdsMessageLog.PUBLISHINGSERVICECENTER, "4");

		return pubMap;
	}

	public static PubMap createPubMap3() {
		PubMap pubMap = new PubMap();
		pubMap.put(IpdsMessageLog.PRODUCTTYPEVALUE, "USGS periodical");
		pubMap.put(IpdsMessageLog.USGSSERIESTYPEVALUE, "Open-File Report");
		pubMap.put(IpdsMessageLog.USGSSERIESLETTER, "a");
		pubMap.put(IpdsMessageLog.FINALTITLE, "My Final Title");
		pubMap.put(IpdsMessageLog.WORKINGTITLE, "My Working Title");
		pubMap.put(IpdsMessageLog.ABSTRACT, "My Abstract");
		pubMap.put(IpdsMessageLog.NONUSGSPUBLISHER, "Not one of those USGS Publishers");
		pubMap.put(IpdsMessageLog.DIGITALOBJECTIDENTIFIER, "doi");
		pubMap.put(IpdsMessageLog.COOPERATORS, "I really want to cooperate");
		pubMap.put(IpdsMessageLog.CITATION, "A short citation");
		pubMap.put(IpdsMessageLog.PHYSICALDESCRIPTION, "physical desc");
		pubMap.put(IpdsMessageLog.PAGERANGE, "pages 1-5");
		pubMap.put(IpdsMessageLog.PRODUCTSUMMARY, "what a summary");
		pubMap.put(IpdsMessageLog.IPNUMBER, "IP1234");
		pubMap.put(IpdsMessageLog.TASK, ProcessType.SPN_PRODUCTION.getIpdsValue());
		pubMap.put(IpdsMessageLog.IPDS_INTERNAL_ID, 453228);
		pubMap.put(IpdsMessageLog.JOURNALTITLE, "An Article");
		pubMap.put(IpdsMessageLog.VOLUME, "V3");
		pubMap.put(IpdsMessageLog.ISSUE, "I3");
		pubMap.put(IpdsMessageLog.EDITIONNUMBER, "E3");
		pubMap.put(IpdsMessageLog.PUBLISHINGSERVICECENTER, "11");

		return pubMap;
	}

	public static PubMap createPubMap4() {
		PubMap pubMap = new PubMap();
		pubMap.put(IpdsMessageLog.PRODUCTTYPEVALUE, "Thesis");
		pubMap.put(IpdsMessageLog.USGSSERIESTYPEVALUE, "Open-File Report");
		pubMap.put(IpdsMessageLog.USGSSERIESLETTER, "a");
		pubMap.put(IpdsMessageLog.FINALTITLE, "My Final Title");
		pubMap.put(IpdsMessageLog.WORKINGTITLE, "My Working Title");
		pubMap.put(IpdsMessageLog.ABSTRACT, "My Abstract");
		pubMap.put(IpdsMessageLog.NONUSGSPUBLISHER, "Not one of those USGS Publishers");
		pubMap.put(IpdsMessageLog.DIGITALOBJECTIDENTIFIER, "doi");
		pubMap.put(IpdsMessageLog.COOPERATORS, "I really want to cooperate");
		pubMap.put(IpdsMessageLog.CITATION, "A short citation");
		pubMap.put(IpdsMessageLog.PHYSICALDESCRIPTION, "physical desc");
		pubMap.put(IpdsMessageLog.PAGERANGE, "pages 1-5");
		pubMap.put(IpdsMessageLog.PRODUCTSUMMARY, "what a summary");
		pubMap.put(IpdsMessageLog.IPNUMBER, "IP1234");
		pubMap.put(IpdsMessageLog.TASK, ProcessType.SPN_PRODUCTION.getIpdsValue());
		pubMap.put(IpdsMessageLog.IPDS_INTERNAL_ID, 453228);
		pubMap.put(IpdsMessageLog.JOURNALTITLE, "A Journal");
		pubMap.put(IpdsMessageLog.VOLUME, "V4");
		pubMap.put(IpdsMessageLog.ISSUE, "I4");
		pubMap.put(IpdsMessageLog.EDITIONNUMBER, "E4");
		pubMap.put(IpdsMessageLog.PUBLISHINGSERVICECENTER, "6");

		return pubMap;
	}

}
