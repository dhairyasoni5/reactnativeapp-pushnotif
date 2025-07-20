if(NOT TARGET hermes-engine::libhermes)
add_library(hermes-engine::libhermes SHARED IMPORTED)
set_target_properties(hermes-engine::libhermes PROPERTIES
    IMPORTED_LOCATION "D:/gradle-cache/caches/8.14.1/transforms/29bab0330702d534e04c2ed5c3711885/transformed/jetified-hermes-android-0.80.1-debug/prefab/modules/libhermes/libs/android.x86/libhermes.so"
    INTERFACE_INCLUDE_DIRECTORIES "D:/gradle-cache/caches/8.14.1/transforms/29bab0330702d534e04c2ed5c3711885/transformed/jetified-hermes-android-0.80.1-debug/prefab/modules/libhermes/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

