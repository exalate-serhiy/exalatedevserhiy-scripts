package domain.mappings

class SidesMapping {
    final Side a
    final Side b
    final String jsonStr

    SidesMapping(Side a, Side b, String jsonStr) {
        this.a = a
        this.b = b
        this.jsonStr = jsonStr
    }
}
