import "./ServerError.css"

type Props = {
    errorMessage: string;
}

export const ServerError = ({
    errorMessage,
    }: Props) => {

    return (
        <>
            {errorMessage !== "" && (
                <p>{errorMessage}</p>
            )}
        </>
    );
}

export default ServerError;