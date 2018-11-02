package edu.clarkson.ee408;

/**
 * A rudimentary arithmetic parser that handles expressions 
 * made of +,-,*,/, () and integers.
 * @Author: Daqing Hou
 * @date: 10/21/2013
 */

public class ParserII {

	public static int DO(String e) throws Exception{
		boolean lastIsOpd;
		
		reset();
		
		int result;
		Token t = new Token(); 
		t.start=0;
		lex(e, t);
		if (t.type==5)
			throw new Exception("malformed expression: empty near "+t.start);
		while (t.type==6){ // ((...
			pushOpr(t.type, t.precedence);
			t.start=t.end+1;
			lex(e, t);
		}
		if (t.type==4){ // an integral number
			pushOpd(Integer.parseInt(e.substring(t.start, t.end+1)));
			lastIsOpd = true;
		}
		else {
			throw new Exception("malformed expression: near "+t.start);
		}

		t.start=t.end+1;
		lex(e, t);
		while (!emptyOpr() || t.type!=5){
			if (t.type==4){ // an integral number
				if (lastIsOpd){
					throw new Exception("malformed expression: missing operator near "+t.start);
				}
				pushOpd(Integer.parseInt(e.substring(t.start, t.end+1)));
				lastIsOpd = true;
			}
			// if operator has lower precedence than operator stack top, and 
			// either it is ), or the top of stack is NOT (.
			else if (!emptyOpr() && t.precedence<=topPrecedence()&&(t.type==7 || topOpr()!=6)){
				lastIsOpd= false;
				while (!emptyOpr() && t.precedence<=topPrecedence()&&(t.type==7 || topOpr()!=6)){
					int result1=0;
					int op = popOpr(); popPrecedence();
					// okay, binary operator
					if (op==0 || op==1 || op==2 || op==3){
						int op2 = popOpd();
						int op1 = popOpd();
						switch (op){
						case 0:
							result1 = op1+op2;
							break;
						case 1:
							result1 = op1-op2;
							break;
						case 2:
							result1 = op1*op2;
							break;
						case 3:
							result1 = op1/op2;
							break;
						}
						pushOpd(result1);
					}
					else if (op==6 && t.type==7){
						break;
					}
					else if (op==6 || t.type==7) {
						throw new Exception("malformed expression: () mismatch near "+t.start);
					}
				}
				if (t.type==5){// we are at end of expression, so try to evaluate everything
					continue;
				}
				if (t.type!=7) // last operator is not )
				pushOpr(t.type, t.precedence);
			}
			else { 
				// if no more operators on stack, or
				//    operator has higher precedence than operator stack top
				//    or it is NOT ) but top of stack is (
				lastIsOpd= false;
				pushOpr(t.type, t.precedence);
			}
			t.start=t.end+1;
			lex(e, t);
		}
		result = popOpd();
		if (!emptyOpd())
			throw new Exception("malformed expression: too many operands");
		return result;
	}
	
	private static class Token {
//		String e;
		public int start, end; // inclusive
		public int type; // -1: error; 0: +; 1: -; 2: *; 3: /; 4: integer; 5: end of e; 6: (; 7: );
		public int precedence; // 0: end; 1: +, - 2: *, /; 10: (; 1: )  
	}
	private static void lex(String e, Token t) throws Exception {
		while (t.start < e.length() && Character.isWhitespace(e.charAt(t.start))){
			++t.start;
		}

		if (t.start == e.length()){
			t.end = t.start;
			t.type = 5;
			t.precedence = 0;
			return;			
		}
		if (e.charAt(t.start)=='+'){
			t.end = t.start;
			t.type = 0;
			t.precedence = 1;
			return;
		}
		if (e.charAt(t.start)=='-'){
			t.end = t.start;
			t.type = 1;
			t.precedence = 1;
			return;
		}
		if (e.charAt(t.start)=='*'){
			t.end = t.start;
			t.type = 2;
			t.precedence = 2;
			return;
		}
		if (e.charAt(t.start)=='/'){
			t.end = t.start;
			t.type = 3;
			t.precedence = 2;
			return;
		}
		if (e.charAt(t.start)=='('){
			t.end = t.start;
			t.type = 6;
			t.precedence = 10;
			return;
		}
		if (e.charAt(t.start)==')'){
			t.end = t.start;
			t.type = 7;
			t.precedence = 1;
			return;
		}
		
		t.type=-1;
		int i=t.start;
		if (Character.isDigit(e.charAt(i))){
			while (i<e.length() && Character.isDigit(e.charAt(i))){
				++i;
			}
			if (i==e.length() || !Character.isDigit(e.charAt(i))){
				t.type = 4;
				t.end=i-1;
			}
		}
		else {
			throw new Exception("malformed expression: unknown symbol near "+t.start);
		}
	}
	
	static boolean emptyOpr(){
		return operatorTop==-1;
	}
	static boolean emptyOpd(){
		return operandTop==-1;
	}
	static void pushOpr(int operator, int precedence){
		++operatorTop;
		operatorStack[operatorTop] = operator;
		precedenceStack[operatorTop] = precedence;
	}
	static int popOpr(){
		--operatorTop;
		return operatorStack[operatorTop+1];
	}
	static int popPrecedence(){
		return precedenceStack[operatorTop+1];
	}
	static int topOpr(){
		return operatorStack[operatorTop];
	}
	static int topPrecedence(){
		return precedenceStack[operatorTop];
	}
	static void pushOpd(int operand){
		++operandTop;
		operandStack[operandTop] = operand;
	}
	static int popOpd() throws Exception{
		--operandTop;
		if (operandTop==-2){
			throw new Exception("malformed expression: too few operands");
		}
		return operandStack[operandTop+1];
	}
	static int topOpd(){
		return operandStack[operandTop];
	}
	static void reset(){
		operatorTop = -1;
		operandTop = -1;
	}
	private static int operatorStack[] = new int[1000];
	private static int precedenceStack[] = new int[1000];
	private static int operatorTop = -1;
	private static int operandStack[] = new int[1001];
	private static int operandTop = -1;
}
