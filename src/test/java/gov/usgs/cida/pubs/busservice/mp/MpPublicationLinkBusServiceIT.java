package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.FullPubsDatabaseSetup;
import gov.usgs.cida.pubs.dao.LinkTypeDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationLinkDao;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublicationLink;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, MpPublication.class, MpPublicationDao.class, LocalValidatorFactoryBean.class,
			PublicationDao.class, MpPublicationLink.class, MpPublicationLinkDao.class,
			LinkType.class, LinkTypeDao.class})
@FullPubsDatabaseSetup
public class MpPublicationLinkBusServiceIT extends BaseIT {

	@Autowired
	public Validator validator;

	private MpPublicationLinkBusService busService;

	@BeforeEach
	public void initTest() throws Exception {
		busService = new MpPublicationLinkBusService(validator);
	}

	@Test
	public void mergeAndDeleteTest() {
		MpPublication mpPub = MpPublication.getDao().getById(2);
		Integer id = MpPublication.getDao().getNewProdId();
		mpPub.setId(id);
		mpPub.setIndexId(String.valueOf(id));
		mpPub.setIpdsId("ipds_" + id);
		mpPub.setLinks(null);
		MpPublication.getDao().add(mpPub);

		//update with no links either side
		busService.merge(id, null);
		Map<String, Object> filters = new HashMap<>();
		filters.put(MpPublicationLinkDao.PUB_SEARCH, id);
		assertEquals(0, MpPublicationLink.getDao().getByMap(filters).size());
		busService.merge(id, new ArrayList<PublicationLink<?>>());
		assertEquals(0, MpPublicationLink.getDao().getByMap(filters).size());

		//Add some links
		Collection<PublicationLink<?>> mpLinks = new ArrayList<>();
		PwPublicationLink mpLink1 = new PwPublicationLink();
		mpLink1.setRank(1);
		LinkType linkType = LinkType.getDao().getById(1);
		mpLink1.setLinkType(linkType);
		mpLinks.add(mpLink1);
		PublicationLink<?> mpLink2 = new PublicationLink<PwPublicationLink>();
		mpLink2.setRank(2);
		mpLink2.setLinkType(linkType);
		mpLinks.add(mpLink2);
		busService.merge(id, mpLinks);
		Collection<MpPublicationLink> addedLinks = MpPublicationLink.getDao().getByMap(filters);
		assertEquals(2, addedLinks.size());
		Map<Integer, Object> linkMap = new HashMap<>();
		boolean gotOne = false;
		boolean gotTwo = false;
		for (MpPublicationLink link : addedLinks) {
			assertEquals(id, link.getPublicationId());
			linkMap.put(link.getId(), link);
			if (1 == link.getRank()) {
				gotOne = true;
			} else if (2 == link.getRank()) {
				gotTwo = true;
			}
		}
		assertTrue(gotOne);
		assertTrue(gotTwo);

		//Now add one, take one away, and update one.
		mpLinks = new ArrayList<>();
		int link2Id = mpLink2.getId();
		mpLink2.setDescription("an updated description");
		mpLinks.add(mpLink2);
		MpPublicationLink mpLink3 = new MpPublicationLink();
		mpLink3.setRank(3);
		mpLink3.setLinkType(linkType);
		mpLinks.add(mpLink3);
		busService.merge(id, mpLinks);
		Collection<MpPublicationLink> updLinks = MpPublicationLink.getDao().getByMap(filters);
		assertEquals(2, updLinks.size());
		gotOne = false;
		gotTwo = false;
		boolean gotThree = false;
		for (MpPublicationLink link : updLinks) {
			assertEquals(id, link.getPublicationId());
			if (1 == link.getRank()) {
				gotOne = true;
			} else if (2 == link.getRank()) {
				gotTwo = true;
				assertEquals(link2Id, link.getId().intValue());
				assertEquals("an updated description", link.getDescription());
			} else if (3 == link.getRank()) {
				gotThree = true;
			}
		}
		assertFalse(gotOne);
		assertTrue(gotTwo);
		assertTrue(gotThree);

		//Now do a straight delete.
		busService.deleteObject(mpLink3);
		updLinks = MpPublicationLink.getDao().getByMap(filters);
		assertEquals(1, updLinks.size());
		gotOne = false;
		gotTwo = false;
		gotThree = false;
		for (MpPublicationLink link : updLinks) {
			assertEquals(id, link.getPublicationId());
			if (1 == link.getRank()) {
				gotOne = true;
			} else if (2 == link.getRank()) {
				gotTwo = true;
			} else if (3 == link.getRank()) {
				gotThree = true;
			}
		}
		assertFalse(gotOne);
		assertTrue(gotTwo);
		assertFalse(gotThree);
	}

}
