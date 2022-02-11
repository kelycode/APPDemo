#pragma once

// Master version numbers
#define PRODUCT_VERSION 1
#define MAJOR_VERSION 0
#define MINOR_VERSION 1
#define PATCH_VERSION 4
// MAJOR表示当前APR的主版本号，它的变化通常意味着APR的巨大的变化，比如体系结构的重新设计，API的重新设计等,这种变化通常会导致APR版本的向前不兼容
// MINOR称之为APR的次版本号，它通常只反映了一些较大的更改，比如APR的API的增加等等，但是这些更改并不影响与旧版本源代码和二进制代码之间的兼容性
// PATCH通常称之为补丁版本，通常情况下如果只是对APR函数的修改而不影响API接口的话都会导致PATCH的变化

#if (PRODUCT_VERSION > 255 || MAJOR_VERSION > 255 || MINOR_VERSION > 255 || PATCH_VERSION > 255)
#error!!! version must be less than 256
#endif

#define ASSEMBLE_VERSION ((PRODUCT_VERSION << 24) | (MAJOR_VERSION << 16) | (MINOR_VERSION << 8) | (PATCH_VERSION << 0))

