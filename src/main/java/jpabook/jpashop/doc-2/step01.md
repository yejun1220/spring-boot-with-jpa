# API 개발 기본 메모 사항들

## static inner class
- static은 하나의 값으로 관리되는데 어차피 (static)클래스라 재생성(생성자에 의해) 되는데 static을 붙일 이유가 있나? or static은 메모리에 하나만 올라가야 하는데 여러개가 생성되나? 라는 충돌적인 의문이 생김  
-> https://siyoon210.tistory.com/141 참조  
-> 서로 다른 참조로 생성되며 static 메모리에 2개가 모두 올라간다.
- 사용하는 이유는?
  - 메모리에 하나만 올리는 것이 아닌 (어차피 여러개 생성된다.) 외부 클래스에 대한 참조가 존재하기 때문에 참조 메모리 공간이 생겨 좋지 않다. 
  - gc가 인스턴스 수거를 하지 못해 메모리 누수가 생길 수 있다.
  