class Log {
    prefix(time, moduleName){
        return `[${time}]【${moduleName}】`;
    }
}

export default new Log();