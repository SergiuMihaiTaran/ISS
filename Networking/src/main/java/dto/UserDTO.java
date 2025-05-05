package dto;

import Domain.TypeOfEmployee;

import java.io.Serializable;

public class UserDTO implements Serializable {



        private String name;
        private String passwd;
        private TypeOfEmployee type;
        public UserDTO(String name) {
            this(name,"",TypeOfEmployee.notSet);
        }

        public UserDTO(String name, String passwd, TypeOfEmployee typeOfEmployee) {
            this.name = name;
            this.passwd = passwd;
            this.type = typeOfEmployee;
        }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public TypeOfEmployee getType() {
        return type;
    }

    public void setType(TypeOfEmployee type) {
        this.type = type;
    }

    public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPasswd() {
            return passwd;
        }

        @Override
        public String toString(){
            return "UserDTO["+name+' '+passwd+"]";
        }
    }


