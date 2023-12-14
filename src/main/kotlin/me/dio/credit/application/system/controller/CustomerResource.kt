package me.dio.credit.application.system.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.dto.request.CustomerUpdateDto
import me.dio.credit.application.system.dto.response.CustomerView
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.service.impl.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Resource", description = "Operations relating to customer resources")
class CustomerResource(
  private val customerService: CustomerService
) {

  @Operation(summary = "Create a new Customer", method = "POST")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Customer added Successfully"),
    ApiResponse(responseCode = "201", description = "Customer added Successfully"),
    ApiResponse(responseCode = "400", description = "Bad Request", content = [Content()]),
    ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
    ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
    ApiResponse(responseCode = "404", description = "Not Found", content = [Content()]),
    ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content()])
  ])
  @PostMapping
  fun saveCustomer(@RequestBody @Valid customerDto: CustomerDto): ResponseEntity<CustomerView> {
    val savedCustomer: Customer = this.customerService.save(customerDto.toEntity())
    return ResponseEntity.status(HttpStatus.CREATED).body(CustomerView(savedCustomer))
  }

  @Operation(summary = "Find Customer by ID", method = "GET")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Find Customer By ID Successfully"),
    ApiResponse(responseCode = "201", description = "Find Customer By ID Successfully"),
    ApiResponse(responseCode = "400", description = "Bad Request", content = [Content()]),
    ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
    ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
    ApiResponse(responseCode = "404", description = "Not Found", content = [Content()]),
    ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content()])
  ])
  @GetMapping("/{id}")
  fun findById(@PathVariable id: Long): ResponseEntity<CustomerView> {
    val customer: Customer = this.customerService.findById(id)
    return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customer))
  }

  @Operation(summary = "Delete Customer")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Deleted Customer Successfully"),
    ApiResponse(responseCode = "201", description = "Deleted Customer Successfully"),
    ApiResponse(responseCode = "400", description = "Bad Request", content = [Content()]),
    ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
    ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
    ApiResponse(responseCode = "404", description = "Not Found", content = [Content()]),
    ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content()])
  ])
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun deleteCustomer(@PathVariable id: Long) = this.customerService.delete(id)

  @Operation(summary = "Update Customer")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Update Customer Successfully"),
    ApiResponse(responseCode = "201", description = "Update Customer Successfully"),
    ApiResponse(responseCode = "400", description = "Bad Request", content = [Content()]),
    ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
    ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
    ApiResponse(responseCode = "404", description = "Not Found", content = [Content()]),
    ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content()])
  ])
  @PatchMapping
  fun upadateCustomer(
    @RequestParam(value = "customerId") id: Long,
    @RequestBody @Valid customerUpdateDto: CustomerUpdateDto
  ): ResponseEntity<CustomerView> {
    val customer: Customer = this.customerService.findById(id)
    val cutomerToUpdate: Customer = customerUpdateDto.toEntity(customer)
    val customerUpdated: Customer = this.customerService.save(cutomerToUpdate)
    return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customerUpdated))
  }
}