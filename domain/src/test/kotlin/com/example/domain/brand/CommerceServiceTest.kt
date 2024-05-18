package com.example.domain.brand

import com.example.domain.brand.enums.Category
import com.example.domain.brand.exception.NotFoundException
import com.example.domain.brand.info.Brand
import com.example.domain.brand.info.BrandItem
import com.example.domain.brand.info.Item
import com.example.domain.brand.info.ModifyBrandItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension::class)
class CommerceServiceTest(
    @Mock
    private val brandStore: BrandStore,
    @Mock
    private val itemStore: ItemStore,
) {
    @InjectMocks
    private lateinit var commerceService: CommerceService

    @Nested
    @DisplayName("브랜드 및 아이템 수정")
    inner class ModifyBrandBrandItemTest {
        @Test
        @DisplayName("brandId가 null인 경우, 브랜드를 생성한다.")
        fun createBrand() {
            val expectedBrand =
                Brand(
                    id = 1,
                    name = "newBrandName",
                )
            val mockModifyBrandItem =
                mock<ModifyBrandItem> {
                    on { brandId } doReturn null
                    on { brandName } doReturn "newBrandName"
                    on { items } doReturn
                        listOf(
                            ModifyBrandItem.BrandItem(
                                id = null,
                                category = Category.TOP,
                                price = 10000,
                            ),
                        )
                    on { toBrand() } doReturn expectedBrand
                }
            whenever(brandStore.save(expectedBrand.name)) doReturn expectedBrand

            val created = commerceService.modifyBrandItem(mockModifyBrandItem)

            assertThat(created.name).isEqualTo("newBrandName")
        }

        @Test
        @DisplayName("brandId가 null이 아니고, brandName이 null이 아닌 경우, 브랜드를 수정한다.")
        fun updateBrand() {
            val expectedBrandName = "updatedBrandName"
            val mockBrand =
                Brand(
                    id = 1,
                    name = "originalBrandName",
                )
            val expectedBrand = mockBrand.copy(name = expectedBrandName)
            val mockModifyBrandItem =
                mock<ModifyBrandItem> {
                    on { brandId } doReturn 1
                    on { brandName } doReturn expectedBrandName
                    on { items } doReturn listOf()
                    on { toBrand() } doReturn expectedBrand
                }

            whenever(brandStore.findById(1)) doReturn mockBrand
            whenever(brandStore.save(expectedBrand)) doReturn expectedBrand

            val updated = commerceService.modifyBrandItem(mockModifyBrandItem)

            assertThat(updated.name).isEqualTo(expectedBrandName)
        }

        @Test
        @DisplayName("brandId가 null이 아니고, brandName이 null인 경우, 브랜드를 삭제한다.")
        fun deleteBrand() {
            val mockBrand =
                Brand(
                    id = 1,
                    name = "originalBrandName",
                )
            val mockModifyBrandItem =
                mock<ModifyBrandItem> {
                    on { brandId } doReturn 1
                    on { brandName } doReturn null
                    on { items } doReturn listOf()
                }

            whenever(brandStore.findById(1)) doReturn mockBrand
            whenever(itemStore.findByBrandId(1)) doReturn emptyList()

            assertDoesNotThrow {
                val deleted = commerceService.modifyBrandItem(mockModifyBrandItem)
                assertThat(deleted.id).isEqualTo(1)
                assertThat(deleted.name).isEqualTo("originalBrandName")
            }
        }
    }

    @Nested
    @DisplayName("최저가 아이템 조회")
    inner class GetLowestPriceItems {
        @Test
        @DisplayName("카테고리별 최저가 아이템을 조회한다.")
        fun getLowestPriceItems() {
            val brand1 =
                Brand(
                    id = 1,
                    name = "brandName",
                )
            val brand2 =
                Brand(
                    id = 2,
                    name = "brandName2",
                )
            val mockItemList =
                listOf(
                    mock<Item> {
                        on { id } doReturn 1
                        on { brandId } doReturn 1
                        on { category } doReturn Category.TOP
                        on { price } doReturn 10000
                        on { toBrandItem(brand1.name) } doReturn
                            BrandItem(
                                id = brand1.id,
                                brandName = brand1.name,
                                category = Category.TOP,
                                price = 10000,
                            )
                    },
                    mock<Item> {
                        on { id } doReturn 3
                        on { brandId } doReturn brand2.id
                        on { category } doReturn Category.BOTTOM
                        on { price } doReturn 20000
                        on { toBrandItem(brand2.name) } doReturn
                            BrandItem(
                                id = brand2.id,
                                brandName = brand2.name,
                                category = Category.BOTTOM,
                                price = 20000,
                            )
                    },
                )

            whenever(itemStore.getLowestPriceItems()) doReturn mockItemList
            whenever(brandStore.findByIds(any())) doReturn listOf(brand1, brand2)

            val brandItems = commerceService.getLowestPriceItem()

            assertThat(brandItems).hasSize(2)
            assertThat(brandItems[0].brandName).isEqualTo(brand1.name)
            assertThat(brandItems[0].category).isEqualTo(Category.TOP)
            assertThat(brandItems[0].price).isEqualTo(10000)
            assertThat(brandItems[1].brandName).isEqualTo(brand2.name)
            assertThat(brandItems[1].category).isEqualTo(Category.BOTTOM)
            assertThat(brandItems[1].price).isEqualTo(20000)
        }

        @Test
        @DisplayName("잘못된 브랜드 id로 조회할 경우, 예외가 발생한다.")
        fun `getLowestPriceItems - brand not found`() {
            val wrongBrandId = 3L
            val brand1 =
                Brand(
                    id = 1,
                    name = "brandName",
                )
            val brand2 =
                Brand(
                    id = wrongBrandId,
                    name = "brandName2",
                )
            val mockItemList =
                listOf(
                    mock<Item> {
                        on { id } doReturn 1
                        on { brandId } doReturn 1
                        on { category } doReturn Category.TOP
                        on { price } doReturn 10000
                        on { toBrandItem(brand1.name) } doReturn
                            BrandItem(
                                id = 1,
                                brandName = brand1.name,
                                category = Category.TOP,
                                price = 10000,
                            )
                    },
                    mock<Item> {
                        on { id } doReturn 3
                        on { brandId } doReturn 2
                        on { category } doReturn Category.BOTTOM
                        on { price } doReturn 20000
                        on { toBrandItem(brand2.name) } doReturn
                            BrandItem(
                                id = 2,
                                brandName = brand2.name,
                                category = Category.BOTTOM,
                                price = 20000,
                            )
                    },
                )

            whenever(itemStore.getLowestPriceItems()) doReturn mockItemList
            whenever(brandStore.findByIds(any())) doReturn listOf(brand1, brand2)

            assertThrows<NotFoundException> {
                commerceService.getLowestPriceItem()
            }
        }
    }

    @Nested
    @DisplayName("최저가 브랜드 세트 조회")
    inner class GetLowestPriceBrandSet {
        @Test
        @DisplayName("최저가 브랜드 세트를 조회한다.")
        fun getLowestPriceBrandSet() {
            val lowestPriceBrand =
                Brand(
                    id = 1,
                    name = "brandName",
                )

            val mockItemList =
                listOf(
                    mock<Item> {
                        on { id } doReturn 1
                        on { brandId } doReturn lowestPriceBrand.id
                        on { category } doReturn Category.TOP
                        on { price } doReturn 10000
                        on { toBrandItem(lowestPriceBrand.name) } doReturn
                            BrandItem(
                                id = lowestPriceBrand.id,
                                brandName = lowestPriceBrand.name,
                                category = Category.TOP,
                                price = 10000,
                            )
                    },
                    mock<Item> {
                        on { id } doReturn 3
                        on { brandId } doReturn lowestPriceBrand.id
                        on { category } doReturn Category.BOTTOM
                        on { price } doReturn 20000
                        on { toBrandItem(lowestPriceBrand.name) } doReturn
                            BrandItem(
                                id = lowestPriceBrand.id,
                                brandName = lowestPriceBrand.name,
                                category = Category.BOTTOM,
                                price = 20000,
                            )
                    },
                )

            whenever(itemStore.getLowestPriceBrandSet()) doReturn mockItemList
            whenever(brandStore.findById(lowestPriceBrand.id)) doReturn lowestPriceBrand

            val brandItems = commerceService.getLowestPriceBrandSet()

            assertThat(brandItems).hasSize(2)
            assertThat(brandItems[0].brandName).isEqualTo(lowestPriceBrand.name)
            assertThat(brandItems[0].category).isEqualTo(Category.TOP)
            assertThat(brandItems[0].price).isEqualTo(10000)
            assertThat(brandItems[1].brandName).isEqualTo(lowestPriceBrand.name)
            assertThat(brandItems[1].category).isEqualTo(Category.BOTTOM)
            assertThat(brandItems[1].price).isEqualTo(20000)
        }

        @Test
        @DisplayName("잘못된 브랜드 id로 조회할 경우, 예외가 발생한다.")
        fun `getLowestPriceBrandSet - brand not found`() {
            val wrongBrandId = 3L
            val lowestPriceBrand =
                Brand(
                    id = 1,
                    name = "brandName",
                )

            val mockItemList =
                listOf(
                    mock<Item> {
                        on { id } doReturn 1
                        on { brandId } doReturn wrongBrandId
                        on { category } doReturn Category.TOP
                        on { price } doReturn 10000
                        on { toBrandItem(lowestPriceBrand.name) } doReturn
                            BrandItem(
                                id = lowestPriceBrand.id,
                                brandName = lowestPriceBrand.name,
                                category = Category.TOP,
                                price = 10000,
                            )
                    },
                    mock<Item> {
                        on { id } doReturn 3
                        on { brandId } doReturn lowestPriceBrand.id
                        on { category } doReturn Category.BOTTOM
                        on { price } doReturn 20000
                        on { toBrandItem(lowestPriceBrand.name) } doReturn
                            BrandItem(
                                id = lowestPriceBrand.id,
                                brandName = lowestPriceBrand.name,
                                category = Category.BOTTOM,
                                price = 20000,
                            )
                    },
                )

            whenever(itemStore.getLowestPriceBrandSet()) doReturn mockItemList
            whenever(brandStore.findById(lowestPriceBrand.id)) doReturn lowestPriceBrand

            assertThrows<Exception> {
                commerceService.getLowestPriceBrandSet()
            }
        }
    }

    @Nested
    @DisplayName("카테고리로 최고가, 최저가 아이템 조회")
    inner class GetBrandPrice {
        @Test
        @DisplayName("카테고리의 최고가, 최저가 아이템을 조회한다.")
        fun findHighestPriceByCategory() {
            val highestPriceBrand =
                Brand(
                    id = 1,
                    name = "brandName",
                )
            val lowestPriceBrand =
                Brand(
                    id = 2,
                    name = "brandName2",
                )
            val requestCategory = Category.TOP
            val highestPriceItem =
                mock<Item> {
                    on { id } doReturn 1
                    on { brandId } doReturn highestPriceBrand.id
                    on { category } doReturn requestCategory
                    on { price } doReturn 100000
                    on { toBrandItem(highestPriceBrand.name) } doReturn
                        BrandItem(
                            id = highestPriceBrand.id,
                            brandName = highestPriceBrand.name,
                            category = requestCategory,
                            price = 100000,
                        )
                }
            val lowestPriceItem =
                mock<Item> {
                    on { id } doReturn 2
                    on { brandId } doReturn lowestPriceBrand.id
                    on { category } doReturn requestCategory
                    on { price } doReturn 10000
                    on { toBrandItem(lowestPriceBrand.name) } doReturn
                        BrandItem(
                            id = lowestPriceBrand.id,
                            brandName = lowestPriceBrand.name,
                            category = requestCategory,
                            price = 10000,
                        )
                }

            whenever(itemStore.findHighestPriceByCategory(requestCategory)) doReturn highestPriceItem
            whenever(brandStore.findById(highestPriceBrand.id)) doReturn highestPriceBrand
            whenever(itemStore.findLowestPriceByCategory(requestCategory)) doReturn lowestPriceItem
            whenever(brandStore.findById(lowestPriceBrand.id)) doReturn lowestPriceBrand

            val item = commerceService.getBrandPrice(requestCategory)

            assertThat(item.highestBrandItem.brandName).isEqualTo(highestPriceBrand.name)
            assertThat(item.highestBrandItem.category).isEqualTo(requestCategory)
            assertThat(item.highestBrandItem.price).isEqualTo(100000)
            assertThat(item.lowestBrandItem.brandName).isEqualTo(lowestPriceBrand.name)
            assertThat(item.lowestBrandItem.category).isEqualTo(requestCategory)
            assertThat(item.lowestBrandItem.price).isEqualTo(10000)
        }

        @Test
        @DisplayName("should throw exception when highest price brand is not found")
        fun `findHighestPriceByCategory - highest price brand not found`() {
            val wrongBrandId = 3L
            val highestPriceBrand =
                Brand(
                    id = 1,
                    name = "brandName",
                )
            val requestCategory = Category.TOP
            val highestPriceItem =
                mock<Item> {
                    on { id } doReturn 1
                    on { brandId } doReturn wrongBrandId
                    on { category } doReturn requestCategory
                    on { price } doReturn 100000
                    on { toBrandItem(highestPriceBrand.name) } doReturn
                        BrandItem(
                            id = highestPriceBrand.id,
                            brandName = highestPriceBrand.name,
                            category = requestCategory,
                            price = 100000,
                        )
                }

            whenever(itemStore.findHighestPriceByCategory(requestCategory)) doReturn highestPriceItem
            whenever(brandStore.findById(highestPriceBrand.id)) doReturn highestPriceBrand

            assertThrows<Exception> {
                commerceService.getBrandPrice(requestCategory)
            }
        }
    }
}
