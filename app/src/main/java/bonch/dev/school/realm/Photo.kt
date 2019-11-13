package bonch.dev.school.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Photo( @PrimaryKey
                   var id: Long = 0,
                   var albumId: Int? = 0,
                   var url: String? = null) : RealmObject() {
}