package org.example.demoapp.resource;

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

import org.example.demoapp.entity.Company;
import org.example.demoapp.entity.Employee;
import org.example.demoapp.service.CompanyService;
import org.example.demoapp.service.EmployeeService;

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
        Optional<Company> optionalCompany = companyService.findById(id);
        if (optionalCompany.isPresent()) {
            companyService.delete(optionalCompany.get());
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("{companyId}/employees")
    public Response addEmployee(@PathParam("companyId") long companyId, long employeeId) {
        Optional<Company> optionalCompany = companyService.findById(companyId);
        if (!optionalCompany.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Optional<Employee> optionalEmployee = employeeService.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Company company = optionalCompany.get();
        boolean employeeAlreadyEmployed = company.isEmployed(employeeId);

        if (employeeAlreadyEmployed) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Employee with id=" + employeeId + " already in company with id=" + companyId).build();
        } else {
            company.addEmployee(optionalEmployee.get());
            companyService.save(company);
            return Response.noContent().build();
        }
    }

    @DELETE
    @Path("{companyId}/employees/{employeeId}")
    public Response removeEmployee(@PathParam("companyId") long companyId, @PathParam("employeeId") long employeeId) {
        Optional<Company> optionalCompany = companyService.findById(companyId);
        if (!optionalCompany.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Optional<Employee> optionalEmployee = employeeService.findById(employeeId);
        if (!optionalEmployee.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Company company = optionalCompany.get();
        company.removeEmployee(optionalEmployee.get());
        companyService.save(company);

        return Response.noContent().build();
    }

}
