package com.revature.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.revature.model.Chef;
import com.revature.dao.ChefDAO;
import com.revature.util.Page;
import com.revature.util.PageOptions;

/**
 * The ChefService class provides services related to Chef objects,
 * including CRUD operations and search functionalities. It acts as a 
 * bridge between the data access layer (ChefDao) and the application 
 * logic, ensuring that all operations on Chef objects are managed 
 * consistently and efficiently.
 */
public class ChefService {

    /** The data access object used for performing operations on Chef entities. */
    @SuppressWarnings("unused")
    private ChefDAO chefDAO;

    /**
     * Constructs a ChefService with the specified ChefDAO.
     *
     * TODO: Finish the implementation so that this class's instance variables are initialized accordingly.
     * 
     * @param chefDao the ChefDao to be used by this service for data access
     */
    public ChefService(ChefDAO chefDAO) {
        this.chefDAO = chefDAO;
    }

    /**
     * TODO: Finds a Chef by their unique identifier.
     *
     * @param id the unique identifier of the chef to be found
     * @return an Optional containing the found Chef if present; 
     *         an empty Optional if not found
     */
    public Optional<Chef> findChef(int id)throws SQLException {
        try {
            Chef chef = chefDAO.getChefById(id);
            return Optional.ofNullable(chef);
        } catch (Exception e) {
            throw new SQLException("chef not found");
        }
        
    }

    /**
     * TODO: Saves a Chef entity. If the Chef's ID is zero, a new Chef is created and the `chef` parameter's ID is updated.
	* 
     * Otherwise, updates the existing Chef.
     *
     * @param chef the Chef entity to be saved or updated
     */
    public void saveChef(Chef chef) throws SQLException{
        if (chef.getId() == 0) {
            int id = chefDAO.createChef(chef);
            chef.setId(id);
        } else {
            chefDAO.updateChef(chef);
        }
    }

    
	/**
     * TODO: Searches for Chefs based on a search term.
     * If the term is null, retrieves all Chefs.
     *
     * @param term the search term for filtering Chefs by attributes
     * @return a list of Chefs matching the search criteria, or all Chefs if term is null
     */
    public List<Chef> searchChefs(String term)throws SQLException {
        if (term == null || term.isBlank()) {
            return chefDAO.getAllChefs();
        }
        return chefDAO.searchChefsByTerm(term);
    }

    /**
     * TODO: Deletes a Chef based on their unique identifier, if they exist.
     *
     * @param id the unique identifier of the Chef to be deleted
     */
    public void deleteChef(int id) throws SQLException{
        Chef chef = chefDAO.getChefById(id);
        if (chef != null) {
            chefDAO.deleteChef(chef);
        }
    }

    /**
     * TODO: Searches for chefs with pagination and sorting options.
     *
     * @param term the search term used to find chefs
     * @param page the page number to retrieve
     * @param pageSize the number of chefs per page
     * @param sortBy the field by which to sort the results
     * @param sortDirection the direction of sorting (ascending or descending)
     * @return a Page containing the results of the search
     */
	
    public Page<Chef> searchChefs(String term, int page, int pageSize, String sortBy, String sortDirection)throws SQLException {
        PageOptions pageOptions = new PageOptions(page, pageSize, sortBy, sortDirection);
        if (term == null || term.isEmpty()) {
            return chefDAO.getAllChefs(pageOptions);
        } else {
            return chefDAO.searchChefsByTerm(term, pageOptions);
        }
    }

    public Chef getChefByCredentials(Chef chef) throws SQLException {
        Chef chef2 = chefDAO.getChefByCredential(chef);
        return chef2;
    }
}

