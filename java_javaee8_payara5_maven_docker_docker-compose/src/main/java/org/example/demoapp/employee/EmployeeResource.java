package org.example.demoapp.employee;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path(EmployeeResource.RESOURCE_BASE_URI)
public class EmployeeResource {

    static final String RESOURCE_BASE_URI = "employees";

    @Inject
    private EmployeeService employeeService;

    @GET
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") long id) {
        Optional<Employee> optionalEmployee = employeeService.findById(id);
        if (optionalEmployee.isPresent()) {
            return Response.ok(optionalEmployee).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response post(Employee employee, @Context UriInfo uriInfo) {
        Employee createdEmployee = employeeService.save(employee);
        URI createdUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(createdEmployee.getId())).build();
        return Response.created(createdUri).build();
    }

    @PUT
    @Path("{id}")
    public Response put(@PathParam("id") long id, Employee employee) {
        try {
            employeeService.updateEmployee(id, employee);
        } catch (EJBException e) { // stateless bean wraps exceptions in EJBException :-/
            if (e.getCausedByException() instanceof EmployeeNotFoundException) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                throw e;
            }
        }

        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id) {
        employeeService.delete(id);
        return Response.noContent().build();

    }

}
