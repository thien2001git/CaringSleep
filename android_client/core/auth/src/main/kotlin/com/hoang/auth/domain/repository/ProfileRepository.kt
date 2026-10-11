package com.hoang.auth.domain.repository

import com.hoang.auth.domain.entity.Profile
import com.hoang.common.domain.repository.BaseCrudRepository

interface ProfileRepository : BaseCrudRepository<Profile, String>
