@RestController
@RequestMapping("employees")
class EmployeeResource {
	
	@GetMapping
	def findAll() {
		return []
	}	
}