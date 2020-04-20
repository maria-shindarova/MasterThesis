package diplomna.clear.ejb.visitor;

import diplomna.clear.tools.token.TokenI;

/**
 *
 * @author Maria Shindarova
 */
public interface VisitorI {
    public void visit(TokenI currToken) throws Exception;
}
