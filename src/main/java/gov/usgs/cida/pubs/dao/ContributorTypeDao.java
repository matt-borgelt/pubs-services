package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class ContributorTypeDao extends BaseDao<ContributorType> {

    private static final String NS = "contributorType";

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public ContributorType getById(Integer domainID) {
        return (ContributorType) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public ContributorType getById(String domainID) {
        return getById(PubsUtilities.parseInteger(domainID));
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<ContributorType> getByMap(Map<String, Object> filters) {
        return getSqlSession().selectList(NS + GET_BY_MAP, filters);
    }

}
