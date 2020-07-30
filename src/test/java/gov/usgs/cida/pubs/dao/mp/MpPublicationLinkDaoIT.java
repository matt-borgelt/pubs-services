package gov.usgs.cida.pubs.dao.mp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.FullPubsDatabaseSetup;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationLink;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, MpPublicationLinkDao.class, MpPublicationDao.class, PwPublicationDao.class})
@FullPubsDatabaseSetup
public class MpPublicationLinkDaoIT extends BaseIT {

	public static final List<String> IGNORE_PROPERTIES = List.of("validationErrors", "valErrors");

	@Autowired
	MpPublicationLinkDao mpPublicationLinkDao;
	@Autowired
	MpPublicationDao mpPublicationDao;
	@Autowired
	PwPublicationDao pwPublicationDao;

	@Test
	public void getbyIdTests() {
		MpPublicationLink link = mpPublicationLinkDao.getById(1);
		assertMpLink1(link);
		link = mpPublicationLinkDao.getById("2");
		assertMpLink2(link);
	}

	@Test
	public void getByMapTests() {
		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 1);
		List<MpPublicationLink> mpLinks = mpPublicationLinkDao.getByMap(filters);
		assertNotNull(mpLinks);
		assertEquals(1, mpLinks.size());
		assertMpLink1((MpPublicationLink) mpLinks.get(0));

		filters.clear();
		filters.put(MpPublicationLinkDao.PUB_SEARCH, 1);
		mpLinks = mpPublicationLinkDao.getByMap(filters);
		assertNotNull(mpLinks);
		assertEquals(2, mpLinks.size());

		filters.clear();
		filters.put(MpPublicationLinkDao.LINK_TYPE_SEARCH, 6);
		mpLinks = mpPublicationLinkDao.getByMap(filters);
		assertNotNull(mpLinks);
		assertEquals(1, mpLinks.size());
	}

	@Test
	public void addUpdateDeleteTest() {
		MpPublicationLink newLink = new MpPublicationLink();
		newLink.setPublicationId(1);
		newLink.setRank(4);
		LinkType linkType = new LinkType();
		linkType.setId(1);
		newLink.setLinkType(linkType);
		newLink.setUrl("http://www.newlink.org");
		newLink.setText("newlink text");
		newLink.setSize("15 bytes");
		newLink.setDescription("my link description");
		newLink.setHelpText("some help stuff");
		LinkFileType linkFileType = new LinkFileType();
		linkFileType.setId(4);
		newLink.setLinkFileType(linkFileType);
		mpPublicationLinkDao.add(newLink);
		
		MpPublicationLink persistedA = mpPublicationLinkDao.getById(newLink.getId());
		assertNotNull(persistedA);
		assertNotNull(persistedA.getId());
		assertDaoTestResults(MpPublicationLink.class, newLink, persistedA, IGNORE_PROPERTIES, true, true);

		persistedA.setRank(5);
		LinkType newLinkType = new LinkType();
		newLinkType.setId(2);
		persistedA.setLinkType(newLinkType);
		persistedA.setUrl("http://www.updated.org");
		persistedA.setText("updated text");
		persistedA.setSize("86 TB");
		newLink.setDescription("my new link description");
		newLink.setHelpText("my new help text");
		LinkFileType newLinkFileType = new LinkFileType();
		newLinkFileType.setId(3);
		persistedA.setLinkFileType(newLinkFileType);
		mpPublicationLinkDao.update(persistedA);

		MpPublicationLink persistedC = mpPublicationLinkDao.getById(newLink.getId());
		assertNotNull(persistedC);
		assertNotNull(persistedC.getId());
		assertDaoTestResults(MpPublicationLink.class, persistedA, persistedC, IGNORE_PROPERTIES, true, true);

		mpPublicationLinkDao.delete(persistedC);
		assertNull(mpPublicationLinkDao.getById(newLink.getId()));

		mpPublicationLinkDao.deleteById(2);
		assertNull(mpPublicationLinkDao.getById(2));

		mpPublicationLinkDao.deleteByParent(1);
		Map<String, Object> filters = new HashMap<>();
		filters.put(MpPublicationLinkDao.PUB_SEARCH, 1);
		List<MpPublicationLink> mpLinks = mpPublicationLinkDao.getByMap(filters);
		assertTrue(mpLinks.isEmpty());
	}

	@Test
	public void copyFromPwTest() {
		mpPublicationDao.copyFromPw(4);
		mpPublicationLinkDao.copyFromPw(4);
		MpPublicationLink link = mpPublicationLinkDao.getById(10);
		assertMpLink10(link);
	}

	@Test
	public void publishToPwTest() {
		mpPublicationLinkDao.publishToPw(null);
		mpPublicationLinkDao.publishToPw(-1);
		mpPublicationDao.publishToPw(1);
		
		//this one should be a straight add.
		mpPublicationLinkDao.publishToPw(1);
		PwPublication pub = pwPublicationDao.getById(1);
		assertEquals(2, pub.getLinks().size());
		for (PublicationLink<?> link : pub.getLinks()) {
			if (1 == link.getId()) {
				assertPwLink1(link);
			} else if (2 == link.getId()) {
				assertPwLink2(link);
			} else {
				fail("Got a bad contributor:" + link.getId());
			}
		}
		
//		//this one should be a merge.
//		mpPublicationDao.copyFromPw(4);
//		MpPublicationCostCenter.getDao().copyFromPw(4);
//		mpPublicationDao.publishToPw(4);
//		MpPublicationCostCenter.getDao().deleteById(10);
//		MpPublicationCostCenter mpPCC = new MpPublicationCostCenter();
//		CostCenter cc = new CostCenter();
//		cc.setId(3);
//		mpPCC.setCostCenter(cc);
//		mpPCC.setPublicationId(4);
//		MpPublicationCostCenter.getDao().add(mpPCC);
//		MpPublicationCostCenter.getDao().publishToPw(4);
//		pub = pwPublicationDao.getById(4);
//		assertEquals(1, pub.getCostCenters().size());
//		for (PublicationCostCenter<?> costCenter : pub.getCostCenters()) {
//			assertPwCostCenterXX(mpPCC.getId(), costCenter);
//		}
	}

	public static void assertPwLink1(PublicationLink<?> link) {
		assertTrue(link instanceof PwPublicationLink);
		assertLink1(link);
	}

	public static void assertMpLink1(PublicationLink<?> link) {
		assertTrue(link instanceof MpPublicationLink);
		assertLink1(link);
	}
	
	public static void assertLink1(PublicationLink<?> link) {
		assertNotNull(link);
		assertEquals(1, link.getId().intValue());
		assertEquals(1, link.getPublicationId().intValue());
		assertEquals(1, link.getRank().intValue());
		assertEquals(1, link.getLinkType().getId().intValue());
		assertEquals("http://www.wow.org", link.getUrl());
		assertEquals("amazing link", link.getText());
		assertEquals("12 GB", link.getSize());
		assertEquals(2, link.getLinkFileType().getId().intValue());
		assertEquals("This description is wow!", link.getDescription());
		assertEquals("some help you are", link.getHelpText());
	}

	public static void assertPwLink2(PublicationLink<?> link) {
		assertTrue(link instanceof PwPublicationLink);
		assertLink2(link);
	}

	public static void assertMpLink2(PublicationLink<?> link) {
		assertTrue(link instanceof MpPublicationLink);
		assertLink2(link);
	}
	
	public static void assertLink2(PublicationLink<?> link) {
		assertNotNull(link);
		assertEquals(2, link.getId().intValue());
		assertEquals(1, link.getPublicationId().intValue());
		assertEquals(2, link.getRank().intValue());
		assertEquals(2, link.getLinkType().getId().intValue());
		assertEquals("http://www.xyz.org", link.getUrl());
		assertEquals("end of the line", link.getText());
		assertEquals("1 TB", link.getSize());
		assertEquals(3, link.getLinkFileType().getId().intValue());
		assertEquals("I'm at the end", link.getDescription());
		assertEquals("I am lotsa help", link.getHelpText());
	}

	public static void assertPwLink10(PublicationLink<?> link) {
		assertTrue(link instanceof PwPublicationLink);
		assertLink10(link);
	}

	public static void assertMpLink10(PublicationLink<?> link) {
		assertTrue(link instanceof MpPublicationLink);
		assertLink10(link);
	}
	
	public static void assertLink10(PublicationLink<?> link) {
		assertNotNull(link);
		assertEquals(10, link.getId().intValue());
		assertEquals(4, link.getPublicationId().intValue());
		assertEquals(1, link.getRank().intValue());
		assertEquals("url", link.getUrl());
		assertEquals("text", link.getText());
		assertEquals("12 GB", link.getSize());
		assertEquals(1, link.getLinkFileType().getId().intValue());
		assertEquals("just a normal description", link.getDescription());
		assertEquals("Super Help", link.getHelpText());
	}
}
