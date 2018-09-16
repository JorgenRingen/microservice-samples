package org.example.demoapp.company;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.example.demoapp.employee.EmployeeNotFoundException;
import org.example.demoapp.employee.EmployeeService;

@Path(CompanyResource.RESOURCE_BASE_URI)
public class CompanyResource {

    static final String RESOURCE_BASE_URI = "companies";

    @Inject
    private CompanyService companyService;

    @Inject
    private EmployeeService employeeService;

    @GET
    public List<Company> findAll() {
        return companyService.findAll();
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") long id) {
        Optional<Company> optionalCompany = companyService.findById(id);
        if (optionalCompany.isPresent()) {
            return Response.ok(optionalCompany).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response post(Company company, @Context UriInfo uriInfo) {
        Company createdCompany = companyService.save(company);
        URI createdUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(createdCompany.getId())).build();
        return Response.created(createdUri).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id) {
        companyService.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("{companyId}/employees")
    public Response addEmployee(@PathParam("companyId") long companyId, long employeeId) {
        try {
            companyService.addEmployee(companyId, employeeId);
        } catch (EJBException e) { // stateless bean wraps exceptions in EJBException :-/
            if (e.getCausedByException() instanceof EmployeeNotFoundException || e.getCausedByException() instanceof CompanyNotFoundException) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else if (e.getCausedByException() instanceof EmployeeAlreadyEmployedInCompanyException) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
            } else {
                throw e;
            }
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("{companyId}/employees/{employeeId}")
    public Response removeEmployee(@PathParam("companyId") long companyId, @PathParam("employeeId") long employeeId) {
        companyService.removeEmployee(companyId, employeeId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
