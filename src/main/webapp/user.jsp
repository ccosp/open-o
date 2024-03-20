<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.context.support.ClassPathXmlApplicationContext" %>
<%@ page import="org.oscarehr.common.dao.UserDao" %>
<%@ page import="java.util.List" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>User List</title>
</head>
<body>
    <h2>User List</h2>
    <table border="1">
        <tr>
            <th>Name</th>
            <th>Age</th>

            <!-- Add more table headers as needed -->
        </tr>
        <!-- <c:forEach var="ds" items="users">
            <tr>
                <td>${ds.getId()} ${ds.getRoleName()}</td>
                Add other columns as needed
            </tr>
        </c:forEach> -->
        <!-- Add table rows to display data -->
    </table>
</body>
</html>
