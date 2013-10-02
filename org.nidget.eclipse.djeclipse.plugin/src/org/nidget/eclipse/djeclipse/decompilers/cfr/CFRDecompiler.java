package org.nidget.eclipse.djeclipse.decompilers.cfr;

import org.benf.cfr.reader.entities.ClassFile;
import org.benf.cfr.reader.entities.Method;
import org.benf.cfr.reader.util.CannotLoadClassException;
import org.benf.cfr.reader.util.ConfusedCFRException;
import org.benf.cfr.reader.util.getopt.BadParametersException;
import org.benf.cfr.reader.util.getopt.CFRState;
import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.output.Dumper;
import org.benf.cfr.reader.util.output.ToStringDumper;
import org.eclipse.core.runtime.IPath;
import org.nidget.eclipse.djeclipse.decompilers.exceptions.DecompilerException;

public class CFRDecompiler {

	public static String decompile(IPath jarPath, IPath classPath) {
		String classPathStr = classPath.toString();
		GetOptParser getOptParser = new GetOptParser();

		try {
			CFRState params = (CFRState)getOptParser.parse(new String[] {classPathStr}, CFRState.getFactory());
			ClassFile c = params.getClassFileMaybePath(params.getFileName());

			params.setClassFileVersion(c.getClassFileVersion());
			try {
				c = params.getClassFile(c.getClassType());
				if (params.analyseInnerClasses()) {
					 c.loadInnerClasses(params);
				}
			} catch (CannotLoadClassException e) {
			}

			c.analyseTop(params);
			Dumper result = new ToStringDumper();
			String methname = params.getMethodName();
			if (methname == null) {
				c.dump(result);
			} else {
				try {
					for (Method method : c.getMethodByName(methname)) {
						method.dump(result, true);
					}
				} catch (NoSuchMethodException e) {
					throw new BadParametersException("No such method '"
							+ methname + "'.", CFRState.getFactory());
				}
			}
			return result.toString();
		} catch (CannotLoadClassException clce) {
			throw new DecompilerException(clce);
		} catch (BadParametersException bpe) {
			throw new DecompilerException(bpe); 
		} catch (ConfusedCFRException ccfre) {
			throw new DecompilerException(ccfre);
		}
	}
}
