package com.rozsa.rpc;

class ParamsDto {
    private Object[] params;

    private int currParamIndex;

    public ParamsDto() {
        currParamIndex = 0;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Object get(int index) {
        if (index < 0 || index >= params.length) {
            return null;
        }

        return params[index];
    }

    public Integer getNextInteger() {
        return getNextDouble().intValue();
    }

    public Double getNextDouble() {
        return (Double)params[currParamIndex++];
    }

    public Object getNext(Class<?> clazz) {

        switch (clazz.getName()) {
            case "Integer":
            case "int":
                return getNextInteger();
            default:
                return clazz.cast(get(currParamIndex++));
        }
    }
}
