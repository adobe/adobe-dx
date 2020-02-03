import React from 'react';

const underlay = (props) => {
    const { open } = props;
    const classNames = ['spectrum-Underlay'];
    open ? classNames.push('is-open') : false;
    return <div className={classNames.join(' ')} />;
}

export default underlay;
