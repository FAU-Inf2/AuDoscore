package asp;
import tester.*;
import java.lang.reflect.*;
import java.lang.invoke.*;
import org.aspectj.lang.reflect.*;

public aspect AllocFactoryAspect{
	pointcut callNew(): call(*.new(..));
	Object around() : callNew() {
		if(!(thisJoinPoint.getSignature() instanceof ConstructorSignature))
			return proceed();
		ConstructorSignature cs = (ConstructorSignature) thisJoinPoint.getSignature();
		Object o = Factory.getInstance(thisJoinPoint.getSignature().getDeclaringType(), cs.getConstructor().getParameterTypes(), thisJoinPoint.getArgs());
		if(o == null)
			return proceed();
		return o;
	}
	pointcut callStatic(): call(public static * *(..)) && !within(asp.*) && !within(java..*) && !within(tester.*) && !within(org.junit.*);
	Object around() : callStatic() {
		Class c = thisJoinPoint.getSignature().getDeclaringType();
		//System.err.println("class: " + c.toString());
		if(Factory.isKnown(c)) {
			//System.err.println("class known: " + c.toString());
			try {
				if(!(thisJoinPoint.getSignature() instanceof MethodSignature))
					return proceed();
				MethodSignature sig = (MethodSignature) thisJoinPoint.getSignature();
				Method m = sig.getMethod();

				String mn = m.getName();

				//System.err.println("meth known?: " + c.getSimpleName() + " " + mn);
				if(Factory.isKnown(c.getSimpleName(), mn)){
					//System.err.println("meth known: " + c.toString() + " " + mn);
					Class cc = Factory.mClassMap.get(c);
					Method cm = cc.getDeclaredMethod(mn, sig.getParameterTypes());
					return cm.invoke(null, thisJoinPoint.getArgs());
				}
			} catch (Exception e){
			}

		}
		return proceed();
	}
}
