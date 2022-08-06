// SPDX-FileCopyrightText: 2019-2020 aTox contributors
//
// SPDX-License-Identifier: GPL-3.0-only

package ltd.evilcorp.atox.newui.contactprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ltd.evilcorp.core.vo.Contact
import ltd.evilcorp.domain.feature.ContactManager
import ltd.evilcorp.domain.tox.PublicKey
import javax.inject.Inject

class ContactProfileViewModel @Inject constructor(contactManager: ContactManager) : ViewModel() {
    var publicKey: PublicKey = PublicKey("")
    val contact: LiveData<Contact> by lazy { contactManager.get(publicKey).asLiveData() }
}
