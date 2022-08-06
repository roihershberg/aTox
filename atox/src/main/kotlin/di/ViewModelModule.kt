// SPDX-FileCopyrightText: 2019-2021 aTox contributors
//
// SPDX-License-Identifier: GPL-3.0-only

package ltd.evilcorp.atox.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import ltd.evilcorp.atox.ui.addcontact.AddContactViewModel
import ltd.evilcorp.atox.ui.call.CallViewModel
import ltd.evilcorp.atox.ui.chat.ChatViewModel
import ltd.evilcorp.atox.ui.contactlist.ContactListViewModel
import ltd.evilcorp.atox.ui.contactprofile.ContactProfileViewModel
import ltd.evilcorp.atox.ui.createprofile.CreateProfileViewModel
import ltd.evilcorp.atox.ui.friendrequest.FriendRequestViewModel
import ltd.evilcorp.atox.ui.settings.SettingsViewModel
import ltd.evilcorp.atox.ui.userprofile.UserProfileViewModel
import kotlin.reflect.KClass
import ltd.evilcorp.atox.newui.addcontact.AddContactViewModel as NewAddContactViewModel
import ltd.evilcorp.atox.newui.chat.ChatViewModel as NewChatViewModel
import ltd.evilcorp.atox.newui.contactlist.ContactListViewModel as NewContactListViewModel
import ltd.evilcorp.atox.newui.contactprofile.ContactProfileViewModel as NewContactProfileViewModel
import ltd.evilcorp.atox.newui.createprofile.CreateProfileViewModel as NewCreateProfileViewModel
import ltd.evilcorp.atox.newui.friendrequest.FriendRequestViewModel as NewFriendRequestViewModel
import ltd.evilcorp.atox.newui.settings.SettingsViewModel as NewSettingsViewModel
import ltd.evilcorp.atox.newui.userprofile.UserProfileViewModel as NewUserProfileViewModel

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AddContactViewModel::class)
    abstract fun bindAddContactViewModel(vm: AddContactViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewAddContactViewModel::class)
    abstract fun bindNewAddContactViewModel(vm: NewAddContactViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CallViewModel::class)
    abstract fun bindCallViewModel(vm: CallViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    abstract fun bindChatViewModel(vm: ChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewChatViewModel::class)
    abstract fun bindNewChatViewModel(vm: NewChatViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactListViewModel::class)
    abstract fun bindContactListViewModel(vm: ContactListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewContactListViewModel::class)
    abstract fun bindNewContactListViewModel(vm: NewContactListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactProfileViewModel::class)
    abstract fun bindContactProfileViewModel(vm: ContactProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewContactProfileViewModel::class)
    abstract fun bindNewContactProfileViewModel(vm: NewContactProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FriendRequestViewModel::class)
    abstract fun bindFriendRequestViewModel(vm: FriendRequestViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewFriendRequestViewModel::class)
    abstract fun bindNewFriendRequestViewModel(vm: NewFriendRequestViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateProfileViewModel::class)
    abstract fun bindProfileViewModel(vm: CreateProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewCreateProfileViewModel::class)
    abstract fun bindNewProfileViewModel(vm: NewCreateProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(vm: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewSettingsViewModel::class)
    abstract fun bindNewSettingsViewModel(vm: NewSettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel::class)
    abstract fun bindUserProfileViewModel(vm: UserProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewUserProfileViewModel::class)
    abstract fun bindNewUserProfileViewModel(vm: NewUserProfileViewModel): ViewModel
}
