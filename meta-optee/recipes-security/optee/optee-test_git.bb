SUMMARY = "OP-TEE sanity testsuite"
HOMEPAGE = "https://github.com/OP-TEE/optee_test"

LICENSE = "BSD & GPLv2"
LIC_FILES_CHKSUM = "file://${S}/LICENSE.md;md5=daa2bcccc666345ab8940aab1315a4fa"

DEPENDS = "optee-client optee-os python-pycrypto-native"

inherit pythonnative

PV = "2.0+git${SRCPV}"

SRC_URI = "git://github.com/OP-TEE/optee_test.git"
S = "${WORKDIR}/git"

SRCREV = "5b41fbff33ff77b19836149fbde0a69524ced089"

OPTEE_CLIENT_EXPORT = "${STAGING_DIR_TARGET}${prefix}"
TEEC_EXPORT         = "${STAGING_DIR_TARGET}${prefix}"
TA_DEV_KIT_DIR      = "${STAGING_INCDIR}/optee/export-user_ta"

# CFLAGS += "-isystem ${STAGING_INCDIR}"

EXTRA_OEMAKE = " TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
                 OPTEE_CLIENT_EXPORT=${OPTEE_CLIENT_EXPORT} \
                 TEEC_EXPORT=${TEEC_EXPORT} \
                 CROSS_COMPILE_HOST=${HOST_PREFIX} \
                 CROSS_COMPILE_TA=${HOST_PREFIX} \
                 V=1 \
               "

do_compile() {
    # *sigh* don't enable -Werror if your code is dodgy and triggers a ton of gcc warnings.
    #sed -i -e 's:-Werror : :g' ${S}/host/xtest/Makefile

    # Top level makefile doesn't seem to handle parallel make gracefully
    echo Host
    ${HOST_PREFIX}gcc --version
    echo Target
    ${TARGET_PREFIX}gcc --version
    ${TARGET_PREFIX}gcc -print-search-dirs
    # die "Debug"
    oe_runmake xtest
    oe_runmake -j1 ta
}

# Imports machine specific configs from staging to build
PACKAGE_ARCH = "${MACHINE_ARCH}"
