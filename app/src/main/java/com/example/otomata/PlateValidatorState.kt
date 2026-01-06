package com.example.otomata

sealed class PlateState{
    open val isAcceptState : Boolean = false

    abstract fun handle(char : Char, context : PlateValidator)

    class QStart : PlateState(){
        override fun handle(char: Char, context: PlateValidator) {
            when (char) {
                '0' -> context.changeState(Q1())
                in '1'..'7' -> context.changeState(Q3())
                '8' -> context.changeState(Q5())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class ErrorState : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {

        }

    }

    class Q1 : PlateState(){
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char in '1'..'9' -> context.changeState(Q2())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class Q2 : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char == ' ' -> context.changeState(QHarf())
                else -> context.changeState(ErrorState())
            }
        }

    }


    class Q3 : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char in '0'..'9' -> context.changeState(Q4())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class Q4 : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char == ' ' -> context.changeState(QHarf())
                else -> context.changeState(ErrorState())
            }
        }

    }


    class Q5 : PlateState(){
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char in '0'..'1' -> context.changeState(Q6())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class Q6 : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char == ' ' -> context.changeState(QHarf())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class QHarf : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when {
            char in 'A'..'Z' && char !in listOf('Q','W','X','Ç','Ş','İ','Ö','Ü','Ğ') -> context.changeState(QTekH())
                else -> context.changeState(ErrorState())
            }
        }
    }

    class QTekH : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char == ' ' -> context.changeState(QR1())
                char in 'A'..'Z' && char !in listOf('Q','W','X','Ç','Ş','İ','Ö','Ü','Ğ') -> context.changeState(QIkiH())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class QIkiH : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char == ' ' -> context.changeState(QX())
                char in 'A'..'Z' && char !in listOf('Q','W','X','Ç','Ş','İ','Ö','Ü','Ğ') -> context.changeState(QUcH())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class QUcH : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char == ' ' -> context.changeState(QY())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class QR1 : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char in '1'..'9' -> context.changeState(QR2())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class QR2 : PlateState(){
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char in '0'..'9' -> context.changeState(QR3())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class QR3 : PlateState(){
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char in '0'..'9' -> context.changeState(QR4())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class QR4 : PlateState(){
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char in '0'..'9' -> context.changeState(QR5())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class QR5 : PlateState() {
        override val isAcceptState = true
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char in '0'..'9' -> context.changeState(QR6())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class QR6 : PlateState() {
        override val isAcceptState = true
        override fun handle(char: Char, context: PlateValidator) {
            context.changeState(ErrorState())
        }

    }

    class QX : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char in '0'..'9' -> context.changeState(QR3())
                else -> context.changeState(ErrorState())
            }
        }

    }

    class QY : PlateState() {
        override fun handle(char: Char, context: PlateValidator) {
            when{
                char in '0'..'9' -> context.changeState(QR4())
                else -> context.changeState(ErrorState())
            }
        }

    }



}



open class PlateValidator{

    private var currentState : PlateState = PlateState.QStart()

    open fun changeState(newState : PlateState){
        this.currentState = newState
    }

    fun validate(plate : String) : Boolean{
        currentState = PlateState.QStart()

        for(char in plate.uppercase()){
            currentState.handle(char,this)

            if(currentState is PlateState.ErrorState) break
        }

        return currentState.isAcceptState
    }

}