package com.pelagohealth.codingchallenge.data.mapper

import com.pelagohealth.codingchallenge.data.datasource.rest.FactDto
import com.pelagohealth.codingchallenge.domain.model.Fact

fun FactDto.toDomain(): Fact =
    Fact(text = this.text, url = this.sourceUrl)
