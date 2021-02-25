import axios from "axios";

const backendPrefix = "/api/user"

const showProfile = () => {
    return axios.get(`${backendPrefix}/profile`)
}

export {
    showProfile,
}