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
}
