package gov.usgs.cida.pubs.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class CorporateContributorValidationTest extends BaseValidatorTest {

	public static final String NOT_NULL_ORGANIZATION = new ValidatorResult("organization", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_USGS = new ValidatorResult("usgs", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_CORPORATION = new ValidatorResult("corporation", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();
	public static final String INVALID_ORGANIZATION_LENGTH = new ValidatorResult("organization", LENGTH_1_TO_XXX_MSG + "400", SeverityLevel.FATAL, null).toString();

	@Autowired
	public Validator validator;

	@Mock
	protected IDao<Contributor<?>> contributorDao;

	private CorporateContributor contributor;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contributor = new CorporateContributor();
		contributor.setContributorDao(contributorDao);
	}

	@Test
	public void notNullTest() {
		contributor.setOrganization(null);
		//voodoo follows
		try {
			Class<?> c = contributor.getClass();
			Field corporation = c.getSuperclass().getDeclaredField("corporation");
			corporation.setAccessible(true);
			corporation.set(contributor, null);
			Field usgs = c.getSuperclass().getDeclaredField("usgs");
			usgs.setAccessible(true);
			usgs.set(contributor, null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		//end voodoo

		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.isValid());
		assertEquals(3, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From CorporateContributor
				NOT_NULL_ORGANIZATION,
				//From Contributor
				NOT_NULL_USGS,
				NOT_NULL_CORPORATION
				);
	}

	@Test
	public void minLengthTest() {
		when(contributorDao.getByMap(anyMap())).thenReturn(new ArrayList<>());
		contributor.setOrganization("");

		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.isValid());
		assertEquals(1, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From CorporateContributor
				INVALID_ORGANIZATION_LENGTH
				);

		contributor.setOrganization("a");
		contributor.setValidationErrors(validator.validate(contributor));
		assertTrue(contributor.isValid());
	}

	@Test
	public void maxLengthTest() {
		when(contributorDao.getByMap(anyMap())).thenReturn(new ArrayList<>());

		contributor.setOrganization(StringUtils.repeat('X', 401));
		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.isValid());
		assertEquals(1, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From CorporateContributor
				INVALID_ORGANIZATION_LENGTH
				);

		contributor.setOrganization(StringUtils.repeat('X', 400));
		contributor.setValidationErrors(validator.validate(contributor));
		assertTrue(contributor.isValid());
	}

}
