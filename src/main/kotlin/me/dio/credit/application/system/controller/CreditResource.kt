package me.dio.credit.application.system.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
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
    ApiResponse(responseCode = "200", description = "Credit added Successfully"),
    ApiResponse(responseCode = "201", description = "Credit added Successfully"),
    ApiResponse(responseCode = "400", description = "Bad Request", content = [Content()]),
    ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
    ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
    ApiResponse(responseCode = "404", description = "Not Found", content = [Content()]),
    ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content()])
  ])
  @PostMapping
  fun saveCredit(@RequestBody @Valid creditDto: CreditDto): ResponseEntity<CreditView> {
      val savedCredit: Credit = creditService.save(creditDto.toEntity())
      return ResponseEntity.status(HttpStatus.CREATED).body(CreditView(savedCredit))
  }

  @Operation(summary = "Find all Credits by CustomerID", method = "GET")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Find all by Customer ID Successfully"),
    ApiResponse(responseCode = "201", description = "Find all by Customer ID Successfully"),
    ApiResponse(responseCode = "400", description = "Bad Request", content = [Content()]),
    ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
    ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
    ApiResponse(responseCode = "404", description = "Not Found", content = [Content()]),
    ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content()])
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

  @Operation(summary = "Find by Credits Customer ID and Credit Code", method = "GET")
  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "Find by Customer ID and Credit Code Successfully"),
    ApiResponse(responseCode = "201", description = "Find by Customer ID and Credit Code Successfully"),
    ApiResponse(responseCode = "400", description = "Bad Request", content = [Content()]),
    ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
    ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
    ApiResponse(responseCode = "404", description = "Not Found", content = [Content()]),
    ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content()])
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