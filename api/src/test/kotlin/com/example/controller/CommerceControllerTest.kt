package com.example.controller

import com.example.TearDownDB
import com.example.TestSpringBaseContext
import com.example.controller.dto.*
import com.example.controller.utils.NumberFormatter
import com.example.domain.brand.BrandStore
import com.example.domain.brand.ItemStore
import com.example.domain.brand.enums.Category
import com.example.domain.brand.info.Brand
import com.example.domain.brand.info.Item
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CommerceControllerTest
    @Autowired
    constructor(
        private val itemStore: ItemStore,
        private val brandStore: BrandStore,
        private val tearDownDB: TearDownDB,
    ) : TestSpringBaseContext() {
        @BeforeEach
        fun setUp() {
            tearDownDB.execute()
        }

        @Nested
        @DisplayName("카테고리별 브랜드 최저가 조회 API")
        inner class GetLowestPriceItems {
            @Test
            @DisplayName("카테고리별 브랜드 최저가 조회 API를 호출하면 성공한다")
            fun `getLowestPriceItems - success`() {
                // given
                val categoryTop = Category.TOP
                val categoryBottom = Category.BOTTOM
                val created1 = brandStore.save("brand1")
                val created2 = brandStore.save("brand2")
                val topLowestPrice = 5000
                val bottomLowestPrice = 10000
                val items =
                    listOf(
                        Item(id = null, brandId = created1.id, category = categoryTop, price = topLowestPrice),
                        Item(id = null, brandId = created1.id, category = categoryBottom, price = 20000),
                        Item(id = null, brandId = created2.id, category = categoryTop, price = 20000),
                        Item(id = null, brandId = created2.id, category = categoryBottom, price = bottomLowestPrice),
                    )
                val expectedTotalPrice = topLowestPrice + bottomLowestPrice
                itemStore.saveAll(items)

                // when
                val responseStr =
                    mockMvc.perform(
                        get("/api/items/lowest-price"),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                // then
                val response = jacksonConfig.objectMapper().readValue<LowestBrandItemDTO>(responseStr)

                assertThat(response.items).hasSize(2)
                assertThat(response.items.find { it.category == categoryTop.desc }?.brandName).isEqualTo(created1.name)
                assertThat(response.items.find { it.category == categoryTop.desc }?.price).isEqualTo("5,000")
                assertThat(response.items.find { it.category == categoryBottom.desc }?.brandName).isEqualTo(created2.name)
                assertThat(response.items.find { it.category == categoryBottom.desc }?.price).isEqualTo("10,000")
                assertThat(response.totalPrice).isEqualTo(NumberFormatter.comma(expectedTotalPrice))
            }

            @Test
            @DisplayName("아이템이 없는 경우 성공한다")
            fun `getLowestPriceItemsWithEmptyItems - success`() {
                // when
                val responseStr =
                    mockMvc.perform(
                        get("/api/items/lowest-price"),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                // then
                val response = jacksonConfig.objectMapper().readValue<LowestBrandItemDTO>(responseStr)

                assertThat(response.items).isEmpty()
                assertThat(response.totalPrice).isEqualTo("0")
            }

            @Test
            @DisplayName("카테고리별 브랜드 최저가 조회 API 존재하지 않는 브랜드가 있는 경우 실패한다")
            fun `getLowestPriceItemsWithNotExistBrand - failed`() {
                // given
                val categoryTop = Category.TOP
                val categoryBottom = Category.BOTTOM
                val created1 = brandStore.save("brand1")
                val wrongBrandId = 3L
                val items =
                    listOf(
                        Item(id = null, brandId = created1.id, category = categoryTop, price = 5000),
                        Item(id = null, brandId = created1.id, category = categoryBottom, price = 20000),
                        Item(id = null, brandId = wrongBrandId, category = categoryTop, price = 20000),
                        Item(id = null, brandId = wrongBrandId, category = categoryBottom, price = 10000),
                    )
                itemStore.saveAll(items)

                // when
                val responseStr =
                    mockMvc.perform(
                        get("/api/items/lowest-price"),
                    )
                        .andExpect(status().isNotFound)
                        .andReturn().response.contentAsString

                val response = jacksonConfig.objectMapper().readValue<ResponseDTO<Any>>(responseStr)
                // then
                assertThat(response.success).isFalse()
            }
        }

        @Nested
        @DisplayName("최저가 브랜드 세트 조회 API")
        inner class GetLowestPriceBrandSet {
            @Test
            @DisplayName("최저가 브랜드 세트 조회 API를 호출하면 성공한다")
            fun `getLowestPriceBrandSet - success`() {
                // given
                val categoryTop = Category.TOP
                val categoryBottom = Category.BOTTOM
                val categorySocks = Category.SOCKS
                val created1 = brandStore.save("brand1")
                created1.let {
                    itemStore.saveAll(
                        listOf(
                            Item(id = null, brandId = created1.id, category = categoryTop, price = 10000),
                            Item(id = null, brandId = created1.id, category = categoryBottom, price = 20000),
                            Item(id = null, brandId = created1.id, category = categorySocks, price = 10000),
                        ),
                    )
                }
                val created2 = brandStore.save("brand2")
                created2.let {
                    itemStore.saveAll(
                        listOf(
                            Item(id = null, brandId = created2.id, category = categoryTop, price = 5000),
                            Item(id = null, brandId = created2.id, category = categoryBottom, price = 10000),
                            Item(id = null, brandId = created2.id, category = categorySocks, price = 5000),
                        ),
                    )
                }

                // when
                val responseStr =
                    mockMvc.perform(
                        get("/api/brands/lowest-price"),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                // then
                val response = jacksonConfig.objectMapper().readValue<LowestBrandItemSetDTO>(responseStr)

                assertThat(response.최저가.브랜드).isEqualTo("brand2")
                assertThat(response.최저가.카테고리).hasSize(3)
                assertThat(response.최저가.카테고리.find { it.카테고리 == categoryTop.desc }?.가격).isEqualTo("5,000")
                assertThat(response.최저가.카테고리.find { it.카테고리 == categoryBottom.desc }?.가격).isEqualTo("10,000")
                assertThat(response.최저가.카테고리.find { it.카테고리 == categorySocks.desc }?.가격).isEqualTo("5,000")
                assertThat(response.최저가.총액).isEqualTo("20,000")
            }

            @Test
            @DisplayName("최저가 브랜드 세트 조회 API 존재하지 않는 브랜드가 있는 경우 실패한다")
            fun `getLowestPriceBrandSetWithNotExistBrand - failed`() {
                // given
                val categoryTop = Category.TOP
                val categoryBottom = Category.BOTTOM
                val categorySocks = Category.SOCKS
                val created1 = brandStore.save("brand1")
                created1.let {
                    itemStore.saveAll(
                        listOf(
                            Item(id = null, brandId = created1.id, category = categoryTop, price = 10000),
                            Item(id = null, brandId = created1.id, category = categoryBottom, price = 20000),
                            Item(id = null, brandId = created1.id, category = categorySocks, price = 10000),
                        ),
                    )
                }
                val wrongBrandId = 3L
                itemStore.saveAll(
                    listOf(
                        Item(id = null, brandId = wrongBrandId, category = categoryTop, price = 5000),
                        Item(id = null, brandId = wrongBrandId, category = categoryBottom, price = 10000),
                        Item(id = null, brandId = wrongBrandId, category = categorySocks, price = 5000),
                    ),
                )

                // when
                val responseStr =
                    mockMvc.perform(
                        get("/api/brands/lowest-price"),
                    )
                        .andExpect(status().isBadRequest)
                        .andReturn().response.contentAsString

                val response = jacksonConfig.objectMapper().readValue<ResponseDTO<Any>>(responseStr)
                // then
                assertThat(response.success).isFalse()
            }
        }

        @Nested
        @DisplayName("카테고리별 최고가, 최저가 조회 API")
        inner class GetBrandPrice {
            @Test
            @DisplayName("카테고리별 최고가, 최저가 조회 API를 호출하면 성공한다")
            fun `getBrandPrice - success`() {
                // given
                val categoryTop = Category.TOP
                val categoryBottom = Category.BOTTOM
                val created1 = brandStore.save("brand1")
                created1.let {
                    itemStore.saveAll(
                        listOf(
                            Item(id = null, brandId = created1.id, category = categoryTop, price = 10000),
                            Item(id = null, brandId = created1.id, category = categoryBottom, price = 20000),
                        ),
                    )
                }
                val created2 = brandStore.save("brand2")
                created2.let {
                    itemStore.saveAll(
                        listOf(
                            Item(id = null, brandId = created2.id, category = categoryTop, price = 5000),
                            Item(id = null, brandId = created2.id, category = categoryBottom, price = 10000),
                        ),
                    )
                }

                // when
                val responseStr =
                    mockMvc.perform(
                        get("/api/brands/price")
                            .param("category", categoryTop.desc),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                // then
                val response = jacksonConfig.objectMapper().readValue<HighestLowestBrandItemDTO>(responseStr)

                assertThat(response.카테고리).isEqualTo(categoryTop.desc)
                assertThat(response.최저가).hasSize(1)
                assertThat(response.최저가.first().브랜드).isEqualTo("brand2")
                assertThat(response.최저가.first().가격).isEqualTo("5,000")
                assertThat(response.최고가).hasSize(1)
                assertThat(response.최고가.first().브랜드).isEqualTo("brand1")
                assertThat(response.최고가.first().가격).isEqualTo("10,000")
            }

            @Test
            @DisplayName("존재하지 않는 카테고리를 요청하는 경우 실패한다")
            fun `getBrandPriceWithNotExistCategory - failed`() {
                val responseStr =
                    mockMvc.perform(
                        get("/api/brands/price")
                            .param("category", "목도리"),
                    )
                        .andExpect(status().isBadRequest)
                        .andReturn().response.contentAsString

                val response = jacksonConfig.objectMapper().readValue<ResponseDTO<Any>>(responseStr)
                assertThat(response.success).isFalse()
                assertThat(response.data).isNull()
                assertThat(response.error?.message).isEqualTo("존재하지 않는 카테고리입니다.")
            }
        }

        @Nested
        @DisplayName("브랜드 및 아이템 수정/등록/삭제")
        inner class ModifyBrand {
            @Test
            @DisplayName("브랜드 등록에 성공한다")
            fun `modifyBrand - create - success`() {
                // given
                val expectedBrandName = "brandName"
                val mockModifyBrandItemDTO =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn null
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn listOf()
                    }

                // when
                val responseStr =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO)),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                // then
                val response = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr)
                assertThat(response.success).isTrue()
                assertThat(response.data).isNotNull
                assertThat(response.data!!.name).isEqualTo(expectedBrandName)
            }

            @Test
            @DisplayName("브랜드 등록에 실패한다")
            fun `modifyBrand - create - failed`() {
                // given
                val mockModifyBrandItemDTO =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn null
                        on { brandName } doReturn null
                        on { items } doReturn listOf()
                    }

                // when
                val responseStr =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO)),
                    )
                        .andExpect(status().isBadRequest)
                        .andReturn().response.contentAsString

                // then
                val response = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr)
                assertThat(response.success).isFalse()
                assertThat(response.data).isNull()
            }

            @Test
            @DisplayName("브랜드명 수정에 성공한다")
            fun `modifyBrand - update - success`() {
                // given
                val expectedBrandName = "brandName"
                val mockModifyBrandItemDTO1 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn null
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn listOf()
                    }

                // when
                val responseStr1 =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO1)),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                val response = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr1)
                val createdBrandId = response.data?.id
                val updatedBrandName = "updatedBrandName"

                val mockModifyBrandItemDTO2 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn createdBrandId
                        on { brandName } doReturn updatedBrandName
                        on { items } doReturn listOf()
                    }

                val responseStr2 =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO2)),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                val updatedResponse = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr2)
                // then
                assertThat(updatedResponse.success).isTrue()
                assertThat(updatedResponse.data).isNotNull
                assertThat(updatedResponse.data!!.name).isEqualTo(updatedBrandName)
            }

            @Test
            @DisplayName("브랜드명 수정에 실패한다")
            fun `modifyBrand - update - failed`() {
                // given
                val expectedBrandName = "brandName"
                val mockModifyBrandItemDTO1 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn null
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn listOf()
                    }

                // when
                mockMvc.perform(
                    patch("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO1)),
                )
                    .andExpect(status().isOk)
                    .andReturn().response.contentAsString

                val wrongBrandId = 100L
                val updateBrandName = "updatedBrandName"

                val mockModifyBrandItemDTO2 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn wrongBrandId
                        on { brandName } doReturn updateBrandName
                        on { items } doReturn listOf()
                    }

                val responseStr2 =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO2)),
                    )
                        .andExpect(status().isBadRequest)
                        .andReturn().response.contentAsString

                val updatedResponse = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr2)
                // then
                assertThat(updatedResponse.success).isFalse()
                assertThat(updatedResponse.data).isNull()
            }

            @Test
            @DisplayName("브랜드 삭제하기")
            fun `modifyBrand - delete - success`() {
                // given
                val expectedBrandName = "brandName"
                val mockModifyBrandItemDTO1 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn null
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn listOf()
                    }

                // when
                val responseStr1 =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO1)),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                val response = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr1)
                val createdBrandId = response.data?.id

                val mockModifyBrandItemDTO2 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn createdBrandId
                        on { brandName } doReturn null
                        on { items } doReturn listOf()
                    }

                val responseStr2 =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO2)),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                val updatedResponse = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr2)
                // then
                assertThat(updatedResponse.success).isTrue()
                assertThat(updatedResponse.data).isNotNull
                assertThat(updatedResponse.data!!.name).isEqualTo(expectedBrandName)
            }

            @Test
            @DisplayName("브랜드 아이템 추가를 성공한다")
            fun `modifyBrand - add item - success`() {
                // given
                val expectedBrandName = "brandName"
                val mockModifyBrandItemDTO1 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn null
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn listOf()
                    }

                // when
                val responseStr1 =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO1)),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                val response = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr1)
                val createdBrandId = response.data?.id

                val mockModifyBrandItemDTO2 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn createdBrandId
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn
                            listOf(
                                ModifyBrandItemDTO.Item(
                                    id = null,
                                    category = Category.TOP,
                                    price = 1000,
                                ),
                            )
                    }

                val responseStr2 =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO2)),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                val updatedResponse = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr2)

                // then
                assertThat(updatedResponse.success).isTrue()
                assertThat(updatedResponse.data).isNotNull
                assertThat(updatedResponse.data!!.name).isEqualTo(expectedBrandName)

                val items = itemStore.findByBrandId(createdBrandId!!)
                assertThat(items).hasSize(1)
                assertThat(items[0].category).isEqualTo(Category.TOP)
                assertThat(items[0].price).isEqualTo(1000)
            }

            @Test
            @DisplayName("브랜드 아이템 수정을 성공한다")
            fun `modifyBrand - update item - success`() {
                // given
                val expectedBrandName = "brandName"
                val mockModifyBrandItemDTO1 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn null
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn listOf()
                    }

                // when
                val responseStr1 =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO1)),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                val response = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr1)
                val createdBrandId = response.data?.id

                val mockModifyBrandItemDTO2 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn createdBrandId
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn
                            listOf(
                                ModifyBrandItemDTO.Item(
                                    id = null,
                                    category = Category.TOP,
                                    price = 1000,
                                ),
                            )
                    }

                mockMvc.perform(
                    patch("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO2)),
                )
                    .andExpect(status().isOk)
                    .andReturn().response.contentAsString

                val brandItems = itemStore.findByBrandId(createdBrandId!!)
                val createdItemId = brandItems[0].id

                val mockModifyBrandItemDTO3 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn createdBrandId
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn
                            listOf(
                                ModifyBrandItemDTO.Item(
                                    id = createdItemId,
                                    category = Category.BOTTOM,
                                    price = 2000,
                                ),
                            )
                    }

                val responseStr3 =
                    mockMvc.perform(
                        patch("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO3)),
                    )
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString

                val updatedResponse2 = jacksonConfig.objectMapper().readValue<ResponseDTO<Brand>>(responseStr3)

                // then
                assertThat(updatedResponse2.success).isTrue()
                assertThat(updatedResponse2.data).isNotNull
                assertThat(updatedResponse2.data!!.name).isEqualTo(expectedBrandName)

                val updatedItems = itemStore.findByBrandId(createdBrandId)
                assertThat(updatedItems).hasSize(1)
                assertThat(updatedItems[0].category).isEqualTo(Category.BOTTOM)
                assertThat(updatedItems[0].price).isEqualTo(2000)
            }

            @Test
            @DisplayName("브랜드 아이템 삭제를 성공한다")
            fun `modifyBrand - delete item - success`() {
                // given
                val expectedBrandName = "brandName"

                val created1 = brandStore.save(expectedBrandName)
                itemStore.saveAll(
                    listOf(
                        Item(id = null, brandId = created1.id, category = Category.TOP, price = 1000),
                    ),
                )

                val mockModifyBrandItemDTO3 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn created1.id
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn listOf()
                    }

                mockMvc.perform(
                    patch("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO3)),
                )
                    .andExpect(status().isOk)
                    .andReturn().response.contentAsString

                // then
                val brandItems = itemStore.findByBrandId(created1.id)
                assertThat(brandItems).isEmpty()
            }

            @Test
            @DisplayName("브랜드 아이템을 동시에 추가/수정/삭제한다")
            fun `modifyBrand - add update delete item - success`() {
                // given
                val expectedBrandName = "brandName"
                val updatedTopPrice = 500
                val expectedSocksPrice = 3000

                val created1 = brandStore.save(expectedBrandName)
                val created1Items =
                    itemStore.saveAll(
                        listOf(
                            Item(id = null, brandId = created1.id, category = Category.TOP, price = 1000),
                            Item(id = null, brandId = created1.id, category = Category.BOTTOM, price = 2000),
                        ),
                    )

                val mockModifyBrandItemDTO3 =
                    mock<ModifyBrandItemDTO> {
                        on { brandId } doReturn created1.id
                        on { brandName } doReturn expectedBrandName
                        on { items } doReturn
                            listOf(
                                ModifyBrandItemDTO.Item(
                                    id = null,
                                    category = Category.SOCKS,
                                    price = expectedSocksPrice,
                                ),
                                ModifyBrandItemDTO.Item(
                                    id = created1Items[0].id,
                                    category = Category.TOP,
                                    price = updatedTopPrice,
                                ),
                            )
                    }

                mockMvc.perform(
                    patch("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonConfig.objectMapper().writeValueAsString(mockModifyBrandItemDTO3)),
                )
                    .andExpect(status().isOk)
                    .andReturn().response.contentAsString

                // then
                val brandItems = itemStore.findByBrandId(created1.id)
                assertThat(brandItems).hasSize(2)
                assertThat(brandItems.find { it.category == Category.SOCKS }?.price).isEqualTo(expectedSocksPrice)
                assertThat(brandItems.find { it.category == Category.TOP }?.price).isEqualTo(updatedTopPrice)
                assertThat(brandItems.find { it.category == Category.BOTTOM }).isNull()
            }
        }
    }
