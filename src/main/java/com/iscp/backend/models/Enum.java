package com.iscp.backend.models;

public interface Enum {
    enum DepartmentType{
        Administration("Administration"),
        SysAdmin("SysAdmin"),
        Human_Resource("Human Resource");

        private final String displayName;

        DepartmentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    enum RoleType {
        Admin,
        Uploader,
        Viewer
    }
    enum PermissionType{
        Admin,
        Uploader,
        Viewer
    }

    enum Periodicity
    {
        Bi_Annually,
        Annually,
        Quarterly,
        Monthly,
        OnEvent
    }

}
