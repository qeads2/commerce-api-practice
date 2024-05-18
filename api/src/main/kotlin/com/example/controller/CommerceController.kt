package com.example.controller

import com.example.controller.dto.*
import com.example.domain.brand.enums.Category
import com.example.domain.brand.info.Brand
import com.example.facade.CommerceFacade
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CommerceController(
    private val commerceFacade: CommerceFacade,
) {
    @GetMapping(value = ["/items/lowest-price"])
    fun getLowestPriceItems(): LowestBrandItemDTO {
        return commerceFacade.getLowestPriceItems()
            .let { LowestBrandItemDTO.from(it) }
    }

    @GetMapping(value = ["/brands/lowest-price"])
    fun getLowestPriceBrandSet(): LowestBrandItemSetDTO {
        return commerceFacade.getLowestPriceBrandSet()
            .let { LowestBrandItemSetDTO.from(it) }
    }

    @GetMapping(value = ["/brands/price"])
    fun getBrandPrice(
        @RequestParam(value = "category") category: String,
    ): HighestLowestBrandItemDTO {
        return commerceFacade.getBrandPrice(Category.getByCategoryName(category))
            .let { HighestLowestBrandItemDTO.from(it) }
    }

    @PatchMapping(value = ["/brands"])
    fun modifyBrandItem(
        @RequestBody modifyBrandItemDTO: ModifyBrandItemDTO,
    ): ResponseDTO<Brand> {
        return ResponseDTO.ofSuccess(commerceFacade.modifyBrandItem(modifyBrandItemDTO.to()))
    }
}
