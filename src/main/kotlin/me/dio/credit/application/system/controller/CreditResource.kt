package me.dio.credit.application.system.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.dto.response.CreditView
import me.dio.credit.application.system.dto.response.CreditViewList
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.service.impl.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
@Tag(name = "Credit Resource", description = "Operations relating to credit resources")

class CreditResource(
  private val creditService: CreditService
) {

  @Operation(summary = "Create a new Credit", method = "POST")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Credit added successfully"),
    ApiResponse(responseCode = "201", description = "Credit added successfully"),
    ApiResponse(responseCode = "400", description = "Bad Request"),
    ApiResponse(responseCode = "401", description = "Unauthorized"),
    ApiResponse(responseCode = "403", description = "Forbidden"),
    ApiResponse(responseCode = "404", description = "Not Found"),
    ApiResponse(responseCode = "500", description = "Internal Server Error"),
  ])
  @PostMapping
  fun saveCredit(@RequestBody @Valid creditDto: CreditDto): ResponseEntity<String> {
      val credit: Credit = creditService.save(creditDto.toEntity())
      val responseMessage = "Credit ${credit.creditCode} - Customer ${credit.customer?.email} saved!"
      return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage)
  }

  @Operation(summary = "Find all by Customer ID", method = "GET")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Find all by Customer ID Successfully"),
    ApiResponse(responseCode = "400", description = "Bad Request"),
    ApiResponse(responseCode = "401", description = "Unauthorized"),
    ApiResponse(responseCode = "403", description = "Forbidden"),
    ApiResponse(responseCode = "404", description = "Not Found"),
    ApiResponse(responseCode = "500", description = "Internal Server Error"),
  ])
  @GetMapping
  fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long):
      ResponseEntity<List<CreditViewList>> {
    val creditViewList: List<CreditViewList> = this.creditService.findAllByCustomer(customerId)
      .stream()
      .map { credit: Credit -> CreditViewList(credit) }
      .collect(Collectors.toList())
    return ResponseEntity.status(HttpStatus.OK).body(creditViewList)
  }

  @Operation(summary = "Find by Customer ID and Credit Code", method = "GET")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Find by Customer ID and Credit Code Successfully"),
    ApiResponse(responseCode = "400", description = "Bad Request"),
    ApiResponse(responseCode = "401", description = "Unauthorized"),
    ApiResponse(responseCode = "403", description = "Forbidden"),
    ApiResponse(responseCode = "404", description = "Not Found"),
    ApiResponse(responseCode = "500", description = "Internal Server Error"),
  ])
  @GetMapping("/{creditCode}")
  fun findByCreditCode(
    @RequestParam(value = "customerId") customerId: Long,
    @PathVariable creditCode: UUID
  ): ResponseEntity<CreditView> {
    val credit: Credit = this.creditService.findByCreditCode(customerId, creditCode)
    return ResponseEntity.status(HttpStatus.OK).body(CreditView(credit))
  }
}