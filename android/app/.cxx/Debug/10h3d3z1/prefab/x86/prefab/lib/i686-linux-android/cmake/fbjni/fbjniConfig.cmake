if(NOT TARGET fbjni::fbjni)
add_library(fbjni::fbjni SHARED IMPORTED)
set_target_properties(fbjni::fbjni PROPERTIES
    IMPORTED_LOCATION "D:/gradle-cache/caches/8.14.1/transforms/aa6f71d6bdddf06715ae4b424cb0d2f9/transformed/jetified-fbjni-0.7.0/prefab/modules/fbjni/libs/android.x86/libfbjni.so"
    INTERFACE_INCLUDE_DIRECTORIES "D:/gradle-cache/caches/8.14.1/transforms/aa6f71d6bdddf06715ae4b424cb0d2f9/transformed/jetified-fbjni-0.7.0/prefab/modules/fbjni/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

