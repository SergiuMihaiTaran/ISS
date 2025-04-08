package dto;

import java.io.Serializable;

public class UserDTO implements Serializable {



        private String name;
        private String passwd;

        public UserDTO(String name) {
            this(name,"");
        }

        public UserDTO(String name, String passwd) {
            this.name = name;
            this.passwd = passwd;
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


