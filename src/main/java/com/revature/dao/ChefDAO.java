package com.revature.dao;
import com.revature.util.ConnectionUtil;
import com.revature.util.Page;
import com.revature.util.PageOptions;
import com.revature.*;

import com.revature.model.Chef;
import java.util.List;

import javax.swing.text.Style;

import java.util.ArrayList;
// import java.beans.Statement;
import java.sql.*;


/**
 * The ChefDAO class abstracts the CRUD operations for Chef objects.
 * It provides functionality to interact with the database for performing 
 * operations such as creating, retrieving, updating, and deleting Chef records. 
 * 
 * The class primarily uses a ConnectionUtil object to connect to the database and includes methods for searching, paginating, and mapping results from database queries.
 */

public class ChefDAO {

    /** A utility class for establishing connections to the database. */
    @SuppressWarnings("unused")
    private ConnectionUtil connectionUtil;

    /** 
     * Constructs a ChefDAO with the specified ConnectionUtil for database connectivity.
     * 
     * TODO: Finish the implementation so that this class's instance variables are initialized accordingly.
     * 
     * @param connectionUtil the utility used to connect to the database
     */
    public ChefDAO(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    /**
     * TODO: Retrieves all chefs from the database.
     * 
     * @return a list of all Chef objects
     */
    public List<Chef> getAllChefs(){
        try (Connection conn = connectionUtil.getConnection()) {
            String sql = "SELECT * FROM CHEF ORDER BY id";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            List<Chef> chefs = new ArrayList<>();
            while (rs.next()) {
                chefs.add(new Chef(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin")
                ));
            }
            return chefs;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all chefs");
        }
    }

    public Chef getChefByCredential(Chef chef){
        try(Connection conn = connectionUtil.getConnection();){
            String sql = "SELECT * FROM CHEF WHERE username = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,chef.getUsername());
            statement.setString(2,chef.getPassword());
            ResultSet resultSet = statement.executeQuery();
                while(resultSet.next()){
                    Chef chef1 = new Chef(resultSet.getInt("id"),
                    resultSet.getString("username"),
                    resultSet.getString("email"),
                    resultSet.getString("password"),
                    resultSet.getBoolean("is_Admin"));
                    return chef1;
                } 
        }catch(SQLException e){
           e.printStackTrace();
        }
        return null;
    }

    /**
     * TODO: Retrieves a paginated list of all chefs from the database.
     * 
     * @param pageOptions options for pagination, including page size and page number
     * @return a paginated list of Chef objects
     */
    public Page<Chef> getAllChefs(PageOptions pageOptions)throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM CHEF");
        if (pageOptions.getSortBy() != null && pageOptions.getSortDirection() != null) {
            sql.append(" ORDER BY ").append(pageOptions.getSortBy())
               .append(" ").append(pageOptions.getSortDirection());
        }
        int pageNumber = Math.max(1, pageOptions.getPageNumber());
        int offset = (pageNumber - 1) * pageOptions.getPageSize();
        sql.append(" LIMIT ? OFFSET ?");
        List<Chef> chefs = new ArrayList<>();
        int totalItems = 0;

        try (Connection conn = connectionUtil.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql.toString())) {
            statement.setInt(1, pageOptions.getPageSize());
            statement.setInt(2, offset);
            ResultSet rs = statement.executeQuery();
            chefs = mapRows(rs);
            String countSql = "SELECT COUNT(*) FROM CHEF";
            try (PreparedStatement countStatement = conn.prepareStatement(countSql)) {
                ResultSet countResultSet = countStatement.executeQuery();
                if (countResultSet.next()) {
                    totalItems = countResultSet.getInt(1);
                }
            }
            int totalPages = (int) Math.ceil((double) totalItems / pageOptions.getPageSize());
            return new Page<>(totalItems, totalPages, pageNumber, pageOptions.getPageSize(), chefs);
        }
    }

    /**
     * TODO: Retrieves a Chef record by its unique identifier.
     *
     * @param id the unique identifier of the Chef to retrieve.
     * @return the Chef object, if found.
     */
    public Chef getChefById(int id) throws SQLException{
        try{
            Connection conn = connectionUtil.getConnection();
            String sql = "SELECT * FROM CHEF WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("connection");
            while(resultSet.next()){
                return new Chef(resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getBoolean("is_Admin"));
            } 
        }catch(SQLException e){
           e.printStackTrace();
        }
        return null;
    }

    /**
     * TODO: Creates a new Chef record in the database.
     *
     * @param chef the Chef object to be created.
     * @return the unique identifier of the created Chef.
     */
    public int createChef(Chef chef) throws SQLException{
        try{
            Connection conn = connectionUtil.getConnection();
            String sql = "INSERT INTO CHEF(username,email,password,is_Admin) VALUES(?,?,?,?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,chef.getUsername());
            statement.setString(2,chef.getEmail()); 
            statement.setString(3,chef.getPassword());
            statement.setBoolean(4,chef.isAdmin()); 

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("CHEF created fail");
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generated_chefId = generatedKeys.getInt(1);
                return generated_chefId;
            }
        }catch (SQLException e){
            throw new SQLException("Chef Created Fial");
        }
        return -1;
    }

    /**
     * TODO: Updates an existing Chef record in the database.
     *
     * @param chef the Chef object containing updated information.
     */
    public void updateChef(Chef chef) {
        try{
            Connection conn = connectionUtil.getConnection();
            String sql = "UPDATE CHEF SET username = ?,email = ?,password= ?,is_Admin = ? WHERE id = ?";
            PreparedStatement st  = conn.prepareStatement(sql);
            st.setString(1, chef.getUsername());
            st.setString(2, chef.getEmail());
            st.setString(3, chef.getPassword());
            st.setBoolean(4, chef.isAdmin());
            st.setInt(5, chef.getId());
            st.executeUpdate();
        }catch(Exception e){
            throw new IllegalArgumentException("Chef is not Updated"); 
        }
    }

    /**
     * TODO: Deletes a Chef record from the database.
     *
     * @param chef the Chef object to be deleted.
     */
    public void deleteChef(Chef chef) {
        try{
            Connection conn = connectionUtil.getConnection();
            String sql = "DELETE FROM CHEF WHERE id = ?";
            PreparedStatement st  = conn.prepareStatement(sql);
            st.setInt(1, chef.getId());
            st.executeUpdate();  
        }catch (Exception e){
            throw new IllegalArgumentException("Chef not deleted");
        }
    }

    /**
     * TODO: Searches for Chef records by a search term in the username.
     *
     * @param term the search term to filter Chef usernames.
     * @return a list of Chef objects that match the search term.
     */
    public List<Chef> searchChefsByTerm(String term) {
        try (Connection conn = connectionUtil.getConnection()) {
            String sql = "SELECT * FROM CHEF WHERE username LIKE ? OR email LIKE ? ORDER BY id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + term + "%");
            ps.setString(2, "%" + term + "%");
            ResultSet rs = ps.executeQuery();

            List<Chef> chefs = new ArrayList<>();
            while (rs.next()) {
                chefs.add(new Chef(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin")
                ));
            }
            return chefs;
        } catch (SQLException e) {
            throw new RuntimeException("Error searching chefs");
        }
    }

    /**
     * TODO: Searches for chefs based on a specified term and returns a paginated result.
     * 
     * @param term the search term to filter chefs by
     * @param pageOptions options for pagination, including page size and page number
     * @return a paginated list of Chef objects that match the search term
     */


    public Page<Chef> searchChefsByTerm(String term, PageOptions pageOptions) {
        StringBuilder sql = new StringBuilder("SELECT * FROM CHEF WHERE username LIKE ? OR email LIKE ?");
        if (pageOptions.getSortBy() != null && pageOptions.getSortDirection() != null) {
            sql.append(" ORDER BY ").append(pageOptions.getSortBy())
            .append(" ").append(pageOptions.getSortDirection());
        }
        int offset = (pageOptions.getPageNumber() - 1) * pageOptions.getPageSize();
        sql.append(" LIMIT ? OFFSET ?");

        List<Chef> chefs = new ArrayList<>();
        int totalItems = 0;

        try (Connection conn = connectionUtil.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql.toString())) {
            statement.setString(1, "%" + term + "%");
            statement.setString(2, "%" + term + "%");
            statement.setInt(3, pageOptions.getPageSize());
            statement.setInt(4, offset);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Chef chef = new Chef(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getBoolean("is_Admin")
                );
                chefs.add(chef);
            }
            String countSql = "SELECT COUNT(*) FROM CHEF WHERE username LIKE ? OR email LIKE ?";
            try (PreparedStatement countStatement = conn.prepareStatement(countSql)) {
                countStatement.setString(1, "%" + term + "%");
                countStatement.setString(2, "%" + term + "%");

                ResultSet countResultSet = countStatement.executeQuery();
                if (countResultSet.next()) {
                    totalItems = countResultSet.getInt(1);
                }
            }
            int totalPages = (int) Math.ceil((double) totalItems / pageOptions.getPageSize());
            return new Page<Chef>(totalItems, totalPages, pageOptions.getPageNumber(), pageOptions.getPageSize(),chefs);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Page<>(0, 0, 0, pageOptions.getPageSize(),new ArrayList<>());
        }
    }

        
        // below are helper methods that are included for your convenience

        /**
         * Maps a single row from the ResultSet to a Chef object.
         *
         * @param set the ResultSet containing Chef data.
         * @return a Chef object representing the row.
         * @throws SQLException if an error occurs while accessing the ResultSet.
         */
        private Chef mapSingleRow(ResultSet set) throws SQLException {
            int id = set.getInt("id");
            String username = set.getString("username");
            String email = set.getString("email");
            String password = set.getString("password");
            boolean isAdmin = set.getBoolean("is_Admin");
            return new Chef(id, username, email, password, isAdmin);
    }

    /**
     * Maps multiple rows from the ResultSet to a list of Chef objects.
     *
     * @param set the ResultSet containing Chef data.
     * @return a list of Chef objects.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private List<Chef> mapRows(ResultSet set) throws SQLException {
        List<Chef> chefs = new ArrayList<>();
        while (set.next()) {
            chefs.add(mapSingleRow(set));
        }
        return chefs;
    }

    /**
     * Paginates the results of a ResultSet into a Page of Chef objects.
     *
     * @param set the ResultSet containing Chef data.
     * @param pageOptions options for pagination and sorting.
     * @return a Page of Chef objects containing the paginated results.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private Page<Chef> pageResults(ResultSet set, PageOptions pageOptions) throws SQLException {
        List<Chef> chefs = mapRows(set);
        int offset = (pageOptions.getPageNumber() - 1) * pageOptions.getPageSize();
        int limit = offset + pageOptions.getPageSize();
        List<Chef> slicedList = sliceList(chefs, offset, limit);
        return new Page<>(pageOptions.getPageNumber(), pageOptions.getPageSize(),
                chefs.size() / pageOptions.getPageSize(), chefs.size(), slicedList);
    }

    /**
     * Slices a list of Chef objects from a starting index to an ending index.
     *
     * @param list the list of Chef objects to slice.
     * @param start the starting index.
     * @param end the ending index.
     * @return a sliced list of Chef objects.
     */
    private List<Chef> sliceList(List<Chef> list, int start, int end) {
        List<Chef> sliced = new ArrayList<>();
        for (int i = start; i < end; i++) {
            sliced.add(list.get(i));
        }
        return sliced;
    }
}

