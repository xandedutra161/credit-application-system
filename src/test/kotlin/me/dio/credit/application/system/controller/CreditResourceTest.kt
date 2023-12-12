package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {

    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var customer: Customer

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
        customer = customerRepository.save(builderCustomerDto().toEntity())
    }

    @AfterEach
    fun tearDown() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
    }

    @Test
    fun `should create a credit and return 201 status`() {
        //given
        val creditDto: CreditDto = buildCreditDto()
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value("500.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value("5"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("xande@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value("1000.0"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer if dayFirstOfInstallment is not in the future and return 400 status`() {
        //given
        val creditDto: CreditDto = buildCreditDto(dayFirstOfInstallment = LocalDate.now())
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details.dayFirstOfInstallment")
                    .value("must be a future date")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save customer if numberOfInstallments is less than 1 and return 400 status`(){
        //given
        val creditDto: CreditDto = buildCreditDto(numberOfInstallments = 0)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details.numberOfInstallments")
                    .value("must be greater than or equal to 1")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save customer if numberOfInstallments is greater than 48 and return 400 status`(){
        //given
        val creditDto: CreditDto = buildCreditDto(numberOfInstallments = 49)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details.numberOfInstallments")
                    .value("must be less than or equal to 48")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save the credit if the customer does not exist and return 400 status`(){
        //given
        val creditDto: CreditDto = buildCreditDto(customerId = 2)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.application.system.exception.BusinessException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details[*]")
                    .value("Id 2 not found")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find credit by creditCode and customerId and return 200 status`() {
        //given
        val credit: Credit = creditRepository.save(buildCreditDto().toEntity())

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${credit.creditCode}?customerId=${credit.customer!!.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").value(credit.creditCode.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(credit.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(credit.numberOfInstallments))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(credit.status.toString()))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find credit by customerId and return 200 status`() {
        //given
        val credit: Credit = creditRepository.save(buildCreditDto().toEntity())

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/customerId=${credit.customer!!.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").value(credit.creditCode.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(credit.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(credit.numberOfInstallments))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(credit.status.toString()))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find credit with invalid customerId and return 400 status`() {
        //given
        val credit: Credit = creditRepository.save(buildCreditDto().toEntity())
        val invalidId: Long = 2
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${credit.creditCode}?customerId=${invalidId}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class java.lang.IllegalArgumentException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").value("Contact admin"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find credit with invalid Credit Code and return 400 status`() {
        //given
        val credit: Credit = creditRepository.save(buildCreditDto().toEntity())
        val invalidCreditCode = "5f06f9fd-7dc7-4dc3-a85e-3a10c30183b1"
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${invalidCreditCode}?customerId=${credit.customer!!.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").value("Creditcode ${invalidCreditCode} not found"))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not find all credits with invalid customerId and return 400 status`() {
        //given
        creditRepository.save(buildCreditDto().toEntity())
        val invalidId: Long = 2
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=${invalidId}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").value("Invalid CustomerId"))
            .andDo(MockMvcResultHandlers.print())

    }

    private fun buildCreditDto(
        creditValue: BigDecimal = BigDecimal.valueOf(500.0),
        dayFirstOfInstallment: LocalDate = LocalDate.now().plusDays(1),
        numberOfInstallments: Int = 5,
        customerId: Long = 1
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstOfInstallment = dayFirstOfInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )

    private fun builderCustomerDto(
        firstName: String = "Alexandre",
        lastName: String = "Dutra",
        cpf: String = "28475934625",
        email: String = "xande@email.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "1234",
        zipCode: String = "000000",
        street: String = "Rua do Xande, 123",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )


}