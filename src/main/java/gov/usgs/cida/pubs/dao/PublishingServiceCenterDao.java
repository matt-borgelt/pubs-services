package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.intfc.IPublishingServiceCenterDao;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class PublishingServiceCenterDao extends BaseDao<PublishingServiceCenter> implements IPublishingServiceCenterDao {

    private static final String NS = "publishingServiceCenter";
	private static final String GET_BY_IPDS_ID = ".getByIpdsId";

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public PublishingServiceCenter getById(Integer domainID) {
        return (PublishingServiceCenter) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public PublishingServiceCenter getById(String domainID) {
        return getById(PubsUtilities.parseInteger(domainID));
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<PublishingServiceCenter> getByMap(Map<String, Object> filters) {
        return getSqlSession().selectList(NS + GET_BY_MAP, filters);
    }

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public PublishingServiceCenter getByIpdsId(Integer ipdsId) {
		return getSqlSession().selectOne(NS + GET_BY_IPDS_ID, ipdsId);
	}

}