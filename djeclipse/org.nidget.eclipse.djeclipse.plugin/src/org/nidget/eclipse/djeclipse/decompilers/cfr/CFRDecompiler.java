package org.nidget.eclipse.djeclipse.decompilers.cfr;

import org.benf.cfr.reader.entities.ClassFile;
import org.benf.cfr.reader.util.CannotLoadClassException;
import org.benf.cfr.reader.util.ConfusedCFRException;
import org.benf.cfr.reader.util.getopt.BadParametersException;
import org.benf.cfr.reader.util.getopt.CFRState;
import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.output.Dumper;
import org.benf.cfr.reader.util.output.ToStringDumper;
import org.nidget.eclipse.djeclipse.decompilers.exceptions.DecompilerException;

public class CFRDecompiler {

	public static String decompile(String classPath) {
		GetOptParser getOptParser = new GetOptParser();

		try {
			CFRState params = (CFRState) getOptParser.parse(new String[] {classPath}, CFRState.getFactory());
			ClassFile c = params.getClassFileMaybePath(classPath, false);

			params.setClassFileVersion(c.getClassFileVersion());

			try {
				c = params.getClassFile(c.getClassType(), true);
			} catch (CannotLoadClassException e) {
			}

			c.analyseTop(params);
			Dumper result = new ToStringDumper();
			String methname = params.getMethodName();
			if (methname == null) {
				c.dump(result);
			} else {
				try {
					c.getMethodByName(methname).dump(result, true);
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
