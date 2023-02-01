package com.testerum.cloud_client.licenses.model.get_updated_licenses

enum class GetUpdatedLicenseStatus {

    UNCHANGED,
    UPDATED,
    NO_VALID_LICENSE, // the user no longer exists, or he no longer has any valid license
    ERROR,            // an error occurred while trying to get the latest license

}
